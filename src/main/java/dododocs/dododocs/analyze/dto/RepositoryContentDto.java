package dododocs.dododocs.analyze.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class RepositoryContentDto {
    private String name;
    private String type; // "file" or "directory"
    private String content; // 파일 내용 (type이 "file"일 때만 사용)
    private List<RepositoryContentDto> children; // 하위 폴더 및 파일
}