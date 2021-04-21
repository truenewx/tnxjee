package ${packageName};

import org.truenewx.tnxjee.repo.jpa.repository.JpaUnityRepository;

import ${entityClassName};

/**
 * @author tnxjee-code-generator
 */
public interface ${repoClassSimpleName} extends JpaUnityRepository<${entityClassSimpleName}, ${keyClassSimpleName}><#if repoxClassSimpleName??>, ${repoxClassSimpleName}</#if> {
}
