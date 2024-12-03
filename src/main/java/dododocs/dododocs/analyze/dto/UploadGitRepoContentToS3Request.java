package dododocs.dododocs.analyze.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UploadGitRepoContentToS3Request {
    private String repositoryName;
    private String branchName;
    private boolean korean;
    private List<String> readmeBlocks;
    /* private boolean previewBlock;
    private boolean overviewBlock;
    private boolean analysisBlock;
    private boolean structureBlock;
    private boolean startBlock;
    private boolean motivationBlock;
    private boolean demoBlock;
    private boolean deploymentBlock;
    private boolean contributorsBlock;
    private boolean faqBlock;
    private boolean performanceBlock; */
}

/*
README_BLOCKS = {
    "PREVIEW_BLOCK": PREVIEW_BLOCK,
    "OVERVIEW_BLOCK": OVERVIEW_BLOCK,
    "ANALYSIS_BLOCK": ANALYSIS_BLOCK,
    "STRUCTURE_BLOCK": STRUCTURE_BLOCK,
    "START_BLOCK": START_BLOCK,
    "MOTIVATION_BLOCK": MOTIVATION_BLOCK,
    "DEMO_BLOCK": DEMO_BLOCK,
    "DEPLOYMENT_BLOCK": DEPLOYMENT_BLOCK,
    "CONTRIBUTORS_BLOCK": CONTRIBUTORS_BLOCK,
    "FAQ_BLOCK": FAQ_BLOCK,
    "PERFORMANCE_BLOCK": PERFORMANCE_BLOCK,
}
 */