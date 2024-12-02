package dododocs.dododocs.analyze.dto;

import lombok.Getter;

@Getter
public class DeleteRepoRegisterRequest {
    private final long registeredRepoId;

    public DeleteRepoRegisterRequest(final long registeredRepoId) {
        this.registeredRepoId = registeredRepoId;
    }
}
