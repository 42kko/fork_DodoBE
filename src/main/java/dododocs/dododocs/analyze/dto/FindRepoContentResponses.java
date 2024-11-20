package dododocs.dododocs.analyze.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class FindRepoContentResponses {
    private List<Map<String, Object>> contents;
}
