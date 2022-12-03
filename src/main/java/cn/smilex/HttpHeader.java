package cn.smilex;

import lombok.Getter;

/**
 * @author smilex
 */
@Getter
public enum HttpHeader {
    CONTENT_TYPE("Content-Type");

    private final String value;

    HttpHeader(String value) {
        this.value = value;
    }
}
