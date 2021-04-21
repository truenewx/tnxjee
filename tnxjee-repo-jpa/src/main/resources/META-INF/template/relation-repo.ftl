package ${packageName};

import org.truenewx.tnxjee.repo.jpa.repository.JpaRelationRepository;
<#if leftKeyClassName??>import ${leftKeyClassName};</#if>
<#if rightKeyClassName??>import ${rightKeyClassName};</#if>

import ${entityClassName};

/**
 * @author tnxjee-code-generator
 */
public interface ${repoClassSimpleName} extends JpaRelationRepository<${entityClassSimpleName}, ${leftKeyClassSimpleName}, ${rightKeyClassSimpleName}>, ${repoxClassSimpleName} {
}
