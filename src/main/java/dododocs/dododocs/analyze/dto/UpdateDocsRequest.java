package dododocs.dododocs.analyze.dto;

import lombok.Getter;

@Getter
public class UpdateDocsRequest {
    private String fileName;

    private UpdateDocsRequest() {
    }

    public UpdateDocsRequest(final String fileName) {
        this.fileName = fileName;
    }
}
