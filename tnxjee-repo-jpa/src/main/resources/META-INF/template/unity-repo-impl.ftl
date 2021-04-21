package ${packageName};

import org.springframework.stereotype.Repository;
import org.truenewx.tnxjee.repo.jpa.support.JpaUnityRepoSupport;
<#if keyClassName??>import ${keyClassName};</#if>

import ${entityClassName};

/**
 * @author tnxjee-code-generator
 */
@Repository
public class ${repoClassSimpleName} extends JpaUnityRepoSupport<${entityClassSimpleName}, ${keyClassSimpleName}> implements ${repoxClassSimpleName} {
}
