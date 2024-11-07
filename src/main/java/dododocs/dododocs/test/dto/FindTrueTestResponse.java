package dododocs.dododocs.test.dto;

import lombok.Getter;

@Getter
public class FindTrueTestResponse {
    private final String detail;

    public FindTrueTestResponse(final String detail) {
        this.detail = detail;
    }
}
