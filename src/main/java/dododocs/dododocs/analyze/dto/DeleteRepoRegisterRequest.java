package dododocs.dododocs.analyze.dto;

import lombok.Getter;

@Getter
public class DeleteRepoRegisterRequest {
    private long registeredRepoId;

    private DeleteRepoRegisterRequest() {
    }

    public DeleteRepoRegisterRequest(final long registeredRepoId) {
        this.registeredRepoId = registeredRepoId;
    }
}
