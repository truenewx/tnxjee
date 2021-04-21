package ${packageName};

import org.truenewx.tnxjee.repo.jpa.repository.JpaUnityRepository;
<#if keyClassName??>import ${keyClassName};</#if>

import ${entityClassName};

/**
 * @author tnxjee-code-generator
 */
public interface ${repoClassSimpleName} extends JpaUnityRepository<${entityClassSimpleName}, ${keyClassSimpleName}>, ${repoxClassSimpleName} {
}
