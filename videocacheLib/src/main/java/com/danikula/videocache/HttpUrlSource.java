package com.danikula.videocache;


import static com.danikula.videocache.Preconditions.checkNotNull;
import static com.danikula.videocache.ProxyCacheUtils.DEFAULT_BUFFER_SIZE;
import static java.net.HttpURLConnection.HTTP_OK;
import static java.net.HttpURLConnection.HTTP_PARTIAL;

import android.text.TextUtils;

import com.danikula.videocache.headers.HeaderInjector;
import com.danikula.videocache.sourcestorage.SourceInfoStorage;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.util.Map;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class HttpUrlSource implements Source {

    private static final int MAX_REDIRECTS = 5;
    public final String url;
    private static final OkHttpClient okHttpClient = new OkHttpClient();
    private Call requestCall = null;
    private InputStream inputStream;
    private volatile long length = Integer.MIN_VALUE;
    private volatile String mime;
    private Map<String, String> headers;

    public HttpUrlSource(String url) {
        this(url, ProxyCacheUtils.getSupposablyMime(url));
    }

    public HttpUrlSource(HttpUrlSource source) {
        this.url = source.url;
        this.mime = source.mime;
        this.length = source.length;
    }

    public HttpUrlSource(String url, String mime) {
        this.url = Preconditions.checkNotNull(url);
        this.mime = mime;
    }

    public HttpUrlSource(String url, Map<String, String> headers) {
        this(url, ProxyCacheUtils.getSupposablyMime(url));
        this.headers = headers;
    }

    public HttpUrlSource(String url, SourceInfoStorage sourceInfoStorage, HeaderInjector headerInjector) {
        checkNotNull(sourceInfoStorage);
        checkNotNull(headerInjector);
        SourceInfo sourceInfo = sourceInfoStorage.get(url);
        if (sourceInfo == null) {
            sourceInfo = new SourceInfo(url, Integer.MIN_VALUE, ProxyCacheUtils.getSupposablyMime(url));
        }
        this.url = sourceInfo.url;
        this.mime = sourceInfo.mime;
        this.length = sourceInfo.length;
        this.headers = headerInjector.addHeaders(url);
    }

    @Override
    public synchronized long length() throws ProxyCacheException {
        if (length == Integer.MIN_VALUE) {
            fetchContentInfo();
        }
        return length;
    }

    @Override
    public void open(long offset) throws ProxyCacheException {
        try {
            Response response = openConnection(offset,  -1);
            mime = response.header("Content-Type");
            inputStream = new BufferedInputStream(response.body().byteStream(), DEFAULT_BUFFER_SIZE);
            length = readSourceAvailableBytes(response, offset, response.code());
        } catch (IOException e) {
            throw new ProxyCacheException("Error opening okHttpClient for " + url + " with offset " + offset, e);
        }
    }

    private long readSourceAvailableBytes(Response response, long offset, int responseCode) throws IOException {
        long contentLength = -1;
        ResponseBody body = response.body();
        if (body != null) {
            contentLength = body.contentLength();
        }
        return responseCode == HTTP_OK ? contentLength : (responseCode == HTTP_PARTIAL ? contentLength + offset : length);
    }

    @Override
    public void close() throws ProxyCacheException {
        if (inputStream != null && requestCall != null) {
            try {
                inputStream.close();
                requestCall.cancel();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public int read(byte[] buffer) throws ProxyCacheException {
        if (inputStream == null) {
            throw new ProxyCacheException("Error reading data from " + url + ": okHttpClient is absent!");
        }
        try {
            return inputStream.read(buffer, 0, buffer.length);
        } catch (InterruptedIOException e) {
            throw new InterruptedProxyCacheException("Reading source " + url + " is interrupted", e);
        } catch (IOException e) {
            throw new ProxyCacheException("Error reading data from " + url, e);
        }
    }

    private void fetchContentInfo() throws ProxyCacheException {
        Response response = null;
        InputStream inputStream = null;
        try {
            response = openConnection(0, 20000);
            ResponseBody body = response.body();
            mime = response.header("Content-Type");
            if (body != null) {
                length = body.contentLength();
                inputStream = body.byteStream();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            ProxyCacheUtils.close(inputStream);
            if (response != null) {
                requestCall.cancel();
            }
        }
    }

    private Response openConnection(long offset, int timeout) throws IOException {
        boolean redirected;
        int redirectCount = 0;
        String url = this.url;
        Request request = null;
        //do {
//      okHttpClient = (HttpURLConnection) new URL(url).openConnection();
        Request.Builder builder = new Request.Builder();
        builder.url(url);
        //flac
        if(headers != null) {
            //设置请求头
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                builder.addHeader(entry.getKey(), entry.getValue());
            }
        }
        if (offset > 0) {
            builder.addHeader("Range", "bytes=" + offset + "-");
        }

        request = builder.build();
        requestCall = okHttpClient.newCall(request);
      /*if (redirected) {
        url = okHttpClient.getHeaderField("Location");
        redirectCount++;
        okHttpClient.disconnect();
      }
      if (redirectCount > MAX_REDIRECTS) {
        throw new ProxyCacheException("Too many redirects: " + redirectCount);
      }*/
        //} while (redirected);

        return requestCall.execute();
    }

    public synchronized String getMime() throws ProxyCacheException {
        if (TextUtils.isEmpty(mime)) {
            fetchContentInfo();
        }
        return mime;
    }

    public String getUrl() {
        return url;
    }

    @Override
    public String toString() {
        return "HttpUrlSource{url='" + url + "}";
    }
}

