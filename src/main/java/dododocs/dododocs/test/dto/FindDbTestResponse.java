package dododocs.dododocs.test.dto;

import lombok.Getter;

@Getter
public class FindDbTestResponse {
    private Long id;
    private String detail;

    public FindDbTestResponse(Long id, String detail) {
        this.id = id;
        this.detail = detail;
    }
}
