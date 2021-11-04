package com.cheney.utils;

public class HttpSupport {

    public static class ContentType {

        public final static String KEY = "Content-Type";

        public final static String APPLICATION_JSON = "application/json";

    }

    public static class ContentEncoding {

        private final static String KEY = "Content-Encoding";

        private final static String HEADER_GZIP = "gzip";

    }

    public static class Method {

        public final static String HTTP_METHOD_GET = "GET";

        public final static String HTTP_METHOD_POST = "POST";

        public final static String HTTP_METHOD_PUT = "PUT";

        public final static String HTTP_METHOD_PATCH = "PATCH";

        public final static String HTTP_METHOD_DELETE = "DELETE";

        public final static String HTTP_METHOD_OPTIONS = "OPTIONS";

    }

}
