package com.tuohang.jfinal.extend.kit;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Map;
import java.util.Map.Entry;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.servlet.http.HttpServletRequest;

import com.jfinal.kit.StrKit;

public class HttpKit {

	private HttpKit() {
	}

	/**
	 * https 域名校验
	 */
	private class TrustAnyHostnameVerifier implements HostnameVerifier {
		public boolean verify(String hostname, SSLSession session) {
			return true;
		}
	}

	/**
	 * https 证书管理
	 */
	private class TrustAnyTrustManager implements X509TrustManager {
		public X509Certificate[] getAcceptedIssuers() {
			return null;
		}

		public void checkClientTrusted(X509Certificate[] chain, String authType)
				throws CertificateException {
		}

		public void checkServerTrusted(X509Certificate[] chain, String authType)
				throws CertificateException {
		}
	}

	private static final String GET = "GET";
	private static final String POST = "POST";
	private static final String PUT = "PUT";
	private static final String DELETE = "DELETE";
	private static final String CHARSET = "UTF-8";

	private static final TrustAnyHostnameVerifier trustAnyHostnameVerifier = new HttpKit().new TrustAnyHostnameVerifier();

	private static SSLSocketFactory initSSLSocketFactory() {
		try {
			TrustManager[] tm = { new HttpKit().new TrustAnyTrustManager() };
			SSLContext sslContext = SSLContext.getInstance("TLS", "SunJSSE");
			sslContext.init(null, tm, new java.security.SecureRandom());
			return sslContext.getSocketFactory();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * HttpKit核心
	 * @param url
	 * @param sslSocketFactory
	 * @param method
	 * @param headers
	 * @return
	 * @throws IOException
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchProviderException
	 * @throws KeyManagementException
	 */
	private static HttpURLConnection getHttpConnection(String url,
			SSLSocketFactory sslSocketFactory, String method,
			Map<String, String> headers) throws IOException,
			NoSuchAlgorithmException, NoSuchProviderException,
			KeyManagementException {
		URL _url = new URL(url);
		HttpURLConnection conn = (HttpURLConnection) _url.openConnection();
		if (conn instanceof HttpsURLConnection) {
			if (sslSocketFactory != null) {
				((HttpsURLConnection) conn)
						.setSSLSocketFactory(sslSocketFactory);
			} else {
				((HttpsURLConnection) conn)
						.setSSLSocketFactory(initSSLSocketFactory());
			}
			((HttpsURLConnection) conn)
					.setHostnameVerifier(trustAnyHostnameVerifier);
		}

		conn.setRequestMethod(method);
		conn.setDoOutput(true);
		conn.setDoInput(true);

		conn.setConnectTimeout(19000);
		conn.setReadTimeout(19000);

		conn.setRequestProperty("Content-Type",
				"application/x-www-form-urlencoded");
		conn.setRequestProperty(
				"User-Agent",
				"Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.146 Safari/537.36");

		if (headers != null && !headers.isEmpty())
			for (Entry<String, String> entry : headers.entrySet())
				conn.setRequestProperty(entry.getKey(), entry.getValue());

		return conn;
	}

	private static String read(String url, SSLSocketFactory sslSocketFactory,
			String method, Map<String, String> queryParas,
			Map<String, String> headers) {
		HttpURLConnection conn = null;
		try {
			conn = getHttpConnection(buildUrlWithQueryString(url, queryParas),
					sslSocketFactory, method, headers);
			conn.connect();
			return readResponseString(conn);
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			if (conn != null) {
				conn.disconnect();
			}
		}
	}

	/**
	 * Send GET request
	 */
	public static String get(String url, SSLSocketFactory sslSocketFactory,
			Map<String, String> queryParas, Map<String, String> headers) {
		return read(url, sslSocketFactory, GET, queryParas, headers);
	}

	public static String get(String url, Map<String, String> queryParas,
			Map<String, String> headers) {
		return get(url, null, queryParas, headers);
	}

	public static String get(String url, Map<String, String> queryParas) {
		return get(url, queryParas, null);
	}

	public static String get(String url) {
		return get(url, null);
	}

	public static byte[] getBytes(String url,
			SSLSocketFactory sslSocketFactory, Map<String, String> queryParas,
			Map<String, String> headers) {
		HttpURLConnection conn = null;
		try {
			conn = getHttpConnection(buildUrlWithQueryString(url, queryParas),
					sslSocketFactory, GET, headers);
			conn.connect();
			return readResponseByte(conn);
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			if (conn != null) {
				conn.disconnect();
			}
		}
	}

	public static byte[] getBytes(String url, Map<String, String> queryParas) {
		return getBytes(url, null, queryParas, null);
	}

	public static byte[] getBytes(String url) {
		return getBytes(url, null);
	}

	/**
	 * Send DELETE request
	 */
	public static String delete(String url, SSLSocketFactory sslSocketFactory,
			Map<String, String> queryParas, Map<String, String> headers) {
		return read(url, sslSocketFactory, DELETE, queryParas, headers);
	}

	public static String delete(String url, Map<String, String> queryParas) {
		return delete(url, null, queryParas, null);
	}

	public static String delete(String url) {
		return delete(url, null, null, null);
	}

	private static String write(String url, SSLSocketFactory sslSocketFactory,
			String method, Map<String, String> queryParas, String data,
			Map<String, String> headers) {
		HttpURLConnection conn = null;
		try {
			conn = getHttpConnection(buildUrlWithQueryString(url, queryParas),
					sslSocketFactory, method, headers);
			conn.connect();

			OutputStream out = conn.getOutputStream();
			out.write(data.getBytes(CHARSET));
			out.flush();
			out.close();

			return readResponseString(conn);
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			if (conn != null) {
				conn.disconnect();
			}
		}
	}

	/**
	 * Send POST request
	 */
	public static String post(String url, SSLSocketFactory sslSocketFactory,
			Map<String, String> queryParas, String data,
			Map<String, String> headers) {
		return write(url, sslSocketFactory, POST, queryParas, data, headers);
	}

	public static String post(String url, Map<String, String> queryParas,
			String data, Map<String, String> headers) {
		return post(url, null, queryParas, data, headers);
	}

	public static String post(String url, Map<String, String> queryParas,
			String data) {
		return post(url, queryParas, data, null);
	}

	public static String post(String url, String data,
			Map<String, String> headers) {
		return post(url, null, data, headers);
	}

	public static String post(String url, String data) {
		return post(url, null, data, null);
	}

	/**
	 * post方法，不带参数（无意义）
	 * 
	 * @param url
	 * @return
	 */
	public static String post(String url) {
		return post(url, null, null, null);
	}

	/**
	 * Send PUT request
	 */
	public static String put(String url, SSLSocketFactory sslSocketFactory,
			Map<String, String> queryParas, String data,
			Map<String, String> headers) {
		return write(url, sslSocketFactory, PUT, queryParas, data, headers);
	}

	public static String put(String url, Map<String, String> queryParas,
			String data, Map<String, String> headers) {
		return put(url, null, queryParas, data, headers);
	}

	public static String put(String url, Map<String, String> queryParas,
			String data) {
		return put(url, queryParas, data, null);
	}

	public static String put(String url, String data,
			Map<String, String> headers) {
		return put(url, null, data, headers);
	}

	public static String put(String url, String data) {
		return put(url, null, data, null);
	}

	private static String readResponseString(HttpURLConnection conn) {
		StringBuilder sb = new StringBuilder();
		InputStream inputStream = null;
		try {
			inputStream = conn.getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					inputStream, CHARSET));
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line).append("\n");
			}
			return sb.toString();
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private static byte[] readResponseByte(HttpURLConnection conn) {
		InputStream inputStream = null;
		ByteArrayOutputStream outStream = null;
		try {
			inputStream = conn.getInputStream();
			outStream = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024];
			int len = 0;
			while ((len = inputStream.read(buffer)) != -1) {
				outStream.write(buffer, 0, len);
				outStream.flush();
			}
			return outStream.toByteArray();
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (outStream != null) {
				try {
					outStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Build queryString of the url
	 */
	private static String buildUrlWithQueryString(String url,
			Map<String, String> queryParas) {
		if (queryParas == null || queryParas.isEmpty())
			return url;

		StringBuilder sb = new StringBuilder(url);
		boolean isFirst;
		if (url.indexOf("?") == -1) {
			isFirst = true;
			sb.append("?");
		} else {
			isFirst = false;
		}

		for (Entry<String, String> entry : queryParas.entrySet()) {
			if (isFirst)
				isFirst = false;
			else
				sb.append("&");

			String key = entry.getKey();
			String value = entry.getValue();
			if (StrKit.notBlank(value))
				try {
					value = URLEncoder.encode(value, CHARSET);
				} catch (UnsupportedEncodingException e) {
					throw new RuntimeException(e);
				}
			sb.append(key).append("=").append(value);
		}
		return sb.toString();
	}

	public static String readIncommingRequestData(HttpServletRequest request) {
		BufferedReader br = null;
		try {
			StringBuilder result = new StringBuilder();
			br = request.getReader();
			for (String line = null; (line = br.readLine()) != null;) {
				result.append(line).append("\n");
			}

			return result.toString();
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			if (br != null)
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
	}

}
