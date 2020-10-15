# JavaEE代码规约
本文档描述JavaEE开发中Java、配置文件、WEB代码的规范（数据库部分见[《数据库开发设计规约》](./database.md)），包括命名、格式、注释以及其它非流程性的规约，用于指导和规范开发人员的代码开发。

本文档参考了《阿里巴巴Java开发手册》，内容根据平日开发实践进行修改调整得出，开发人员需严格遵守，如对规约有异议可提出，经讨论确认后修订本文档，但在修订前仍需按照原规约执行。

## 一、通用规约
本小节描述在所有代码中都通用的规约。
### 1.1 命名
现代软件系统的复杂度，对参与人员尤其是开发人员的协同性提出了极高的要求。
命名，是多方协同过程中至关重要的基础。
可以说，良好的命名可以避免沟通交流中80%以上的问题。
请高度重视命名的重要性，不要有随便命名、代码能跑即可的思想。
花在命名上的时间，会十倍百倍地减少你与他人沟通时的时间浪费。
1. 【强制】命名必须准确、清晰易懂、无歧义、常用非生僻、与实际含义吻合，让读者能见名知意。
2. 【强制】使用英文单词及其组合，不得随意使用拼音、拼音缩写、中文。
  > 例外1：对外宣传用的网页URL地址中可使用拼音，以利于用户记忆  
  > 例外2：公司、品牌、产品名称、地名等本身即为拼音的，可直接引用，如：alibaba/taobao/hangzhou
3. 【强制】任何地方均避免使用任何语言的可能涉嫌种族歧视的词语
  > 替换：blackList -> blockList，whileList -> allowList，master -> main / primary，slave -> secondary
4. 【强制】不能随意缩简单词，只有常用的、约定俗成的缩写和简写才可使用。下表列出可使用的缩写和简写，如要使用该表中没有的缩写或简写，需申请将缩写或简写加入该表后方可使用：

    全名|中文含义|缩写/简写|禁用场景
    ---|---|---|---
    Authority|权限|Auth|
    Average|平均|Avg|
    Business|商业|Biz|命名短
    Business to Customer|商家对个人|B2c|
    Certificate|证书|Cert|
    Corporation|企业|Corp|
    Customer to Customer|个人对个人|C2c|
    Department|部门|Dept|
    Document|文档|Doc|
    Extensible Markup Language|可扩展标记语言|Xml|
    For|为了…|4|首部
    HyperText Transfer Protocol|超文本传输协议|Http|
    Identification|标识|Id|
    Information|信息|Info|
    Maximum/Maximal|最大值/最大的|Max|
    Minimum/Minimal|最小值/最小的|Min|
    Number|编号（有序的）|No|单独
    Number|号码（无序的）|Num|单独
    Library|库|Lib|
    Organization|组织|Org|
    Register|登记/注册|Reg|
    Statistics|统计|Stat|
    Telephone|电话|Tel|
    Televition|电视|Tv|
    To|成为…|2|首部
    Transaction|交易|Trans|表示事务的含义
    Unified Modeling Language|统一建模语言|Uml|
    Window|窗体|Win|
5. 【强制】缩写和简写一律视为一个完整单词，在驼峰法中表现为xxx/Xxx形式，不采用全大写的形式
6. 【强制】对于中文词在英文中存在多个相近单词的情况，除优先采用简单、易懂、常用的单词外，规定以下对照单词：
    
    中文|英文|说明
    ---|---|---
    查找|Find|查找一个或多个实体
    查询|Query|分页查询实体清单和分页信息
    获取|Get|获取一个实体的属性
    添加|Add|
    修改|Update|
    删除|Delete|从存储介质中删除
    移除|Remove|从一个范围或集合中移除
    生成|Generate|按照一定的业务规则生成一个字符串或数值
    分配|Assign|
    确保|Ensure|
    
7. 【推荐】如有命名无法确定，可提出多人讨论，甚至提请技术总监参与，即使开会两小时探讨，都是可以接受的。

### 1.2 格式
1. 【强制】json文件和内容缩进使用2位空格，Tab替换为2位空格；其它所有文本的缩进一律使用4位空格，Tab替换为4位空格
2. 【强制】所有文本行的最大宽度均为120个字符，设置代码格式化程序以该数值为基准进行自动格式化
3. 【强制】所有文本文件的字符编码格式统一为UTF-8
4. 【推荐】所有文本文件的最后以一个空行结束，以便于SCM工具辨别文件结尾

## 二、Java规约
### 2.1 命名
- 【强制】所有命名均不能以_和$开头，也不能以_和$结尾。
- 工程结构
  1. 【强制】Java工程均通过maven创建，其基本目录结构和命名遵从maven的规范和习惯，maven工程的artifactId即为工程名。
  2. 【强制】工程名都只使用小写英文字母，单词之间用-分隔。
  3. 【强制】一个典型的maven工程，由一个pom类型的顶级父工程，和多个子工程组成。
对于分布式微服务架构的工程，这些子工程为微服务子工程，也是pom类型的maven工程，其下包含各层级的子工程。

    层级子工程|命名方式|范例|说明
    ---|---|---|---
    api|[顶级工程名]-[微服务名]-api|tnxsample-admin-api|微服务对外提供服务的接口定义模块
    model|[顶级工程名]-[微服务名]-model|tnxsample-admin-model|微服务的领域模型定义模块
    repo|[顶级工程名]-[微服务名]-repo|tnxsample-admin-repo|微服务内部的数据持久化模块
    service|[顶级工程名]-[微服务名]-service|tnxsample-admin-service|微服务内部的业务接口定义和逻辑实现模块
    web|[顶级工程名]-[微服务名]-web|tnxsample-admin-web|微服务的视图模块
    
  > 说明：层级子工程之间的依赖关系详见样例工程 [tnxsample](https://github.com/truenewx/tnxsample)
- 包名
  1. 【强制】包名一律小写，点分隔符之间使用尽量少的英语单词，以一个自然语义的英语单词为佳。
  2. 【强制】每一级包名都必须为名词，且为单数形式。
  3. 【强制】以maven工程的groupId开头，将artifactId中的-替换为点分隔符，去掉重叠部分，用点分隔符拼接形成工程的顶级包名。
  4. 【推荐】可以使用符合规范的缩写。
  > 例如：tnxjee-core工程，groupId=org.truenewx.tnxjee，artifactId=tnxjee-core，其顶级包名为org.truenewx.tnxjee.core
- 接口名
  1. 【强制】采用驼峰法，UpperCamelCase风格，首字母大写，不能包含下划线。
  2. 【强制】必须为名词或形容词或它们的短语。
  3. 【推荐】以形容词或形容词短语命名的接口，一般表示其具有某种能力，采用一些惯用的后缀。
  - -able后缀，如Java SE中的Cloneable，Comparable等;
  - -Aware后缀，如Spring中的ApplicationContextAware表示可设置ApplicationContext；
  - -Source后缀，如Spring中的MessageSource表示可获取Message；
  - -Ware后缀，既可设置又可获取某种事物的接口（比较少见）；
  > 说明：可以多参考JDK和Spring框架中的命名习惯。
  4. 【强制】从JpaRepository继承的实体Repo的接口名形如：[实体类名]Repo，自定义的实体Repo接口名形如：[实体类名]Repox，Service接口名形如：[实体类名]Service
  > 例如：UserRepo，UserRepox，UserService
  5. 【强制】不使用特殊的前缀，如：I-。
  > 正例：UserService  
  > 反例：IUserService
- 类名
  1. 【强制】采用驼峰法，UpperCamelCase风格，首字母大写，不能包含下划线。
  2. 【强制】必须为名词或名词短语。
  3. 【强制】模型类名必须与自然系统中的对应事物吻合，头尾不要加对命名含义没有实际影响的前缀或后缀，如果一个前缀或后缀去掉之后不影响该命名的含义，则要坚决去掉。
  如比较常见的-Info后缀，User（用户）和UserInfo（用户信息），前者更加简明扼要，更符合面向对象的思想。
  4. 【强制】模型类名不要加PO/DTO/BO/VO这种技术功能性缩写词后缀。
  这首先违反了缩写作为一个完整单词的书写形式，其次将技术特性包含在反应自然系统事物的业务名称中，不符合面向对象思想。
    - PO即持久化对象，对应了本框架中的Entity（实体），不应该有任何多余的前缀后缀；
    - DTO即数据传输对象，对应了本框架中的CommandModel、QueryModel和QueryResult，前两者的实现类应使用XxxCommand和XxxQuerying的命名形式，查询结果直接使用QueryResult类；
    - BO即业务逻辑对象，在Service层处理复杂数据结构时使用，应该使用符合业务场景的名称，而不是简单的用领域名称+BO了事，场景可能很多，领域名称+BO很可能不够用，也无法反应场景特征，增加后续读者的理解难度；
    - VO即视图对象，与BO同理，视图场景可能不止一个，应该使用符合视图场景的名称；
  5. 【强制】Repo实现类名形如：[实体类名]RepoImpl，Service实现类名形如：[实体类名]ServiceImpl。
  > 例如：UserRepoImpl，UserServiceImpl
  6. 【推荐】抽象类使用Abstract-前缀；异常类使用-Exception后缀；单元测试类名形如：[被测试类名]Test；
- 方法名
  1. 【强制】采用驼峰法，lowerCamelCase风格，首字母小写，不能包含下划线。
  2. 【推荐】一般采用动词或动宾短语或动词+副词，少数情况下可使用名词，使用名词时意为获取其值，如：size()意为获取大小。
  3. 【强制】Repo接口中的查询方法，遵循spring-data-jpa的方法命名规则，详见[官方文档](https://docs.spring.io/spring-data/jpa/docs/2.2.x/reference/html/#jpa.query-methods.query-creation)
  4. 【强制】无法通过spring-data-jpa自动实现机制实现的方法需定义Repox接口，并提供RepoImpl实现类实现，其方法名遵循以下规则：
    - get-，表示获取当前实体的属性或引用实体；
    - count-，表示取数，其结果必须为long类型；
    - query-，表示分页查询，其结果必须为QueryResult<实体类型>类型；
    - update-，表示修改，其结果必须为long，为受影响的数据行数；
    - delete-，表示删除，其结果必须为long，为受影响的数据行数；
  5. 【推荐】Service中的方法从业务角度进行命名，除find、update等惯用词要求外，命名应与实际业务吻合，能准确、无歧义地反应该方法的业务效果。
  6. 【强制】Controller中的方法对应URL，采用一种从Restful改造的风格，其规则及与URL的对应关系将在后续URL章节进行说明。
- 属性/参数/局部变量
  1. 【强制】静态static属性（无论是否final）均使用全大写，单词之间用下划线分隔；
  非静态属性、方法参数、局部变量均采用驼峰法，lowerCamelCase风格，首字母小写，不能包含下划线。
  2. 【强制】一般类型使用名词或名词短语，布尔类型使用形容词或形容词短语。
  > 特殊情况：表示是否某种身份或角色时，命名应该形如：if[名词]，如：boolean ifManager表示是否管理员，不能以is-前缀命名。
  3. 【推荐】有时，非简单类型的属性/参数/局部变量的所属接口或类名很长，如果将接口或类名首字母小写后作为属性/参数/局部变量的名称，可能导致行宽过大。
  推荐将接口或类名中每个单词的首字母拼凑在一起并小写作为命名。我们允许参数和局部变量采用这种缩写方式命名，但接口和类的属性不能使用这种方式。
  > 例如：BodyResolvableExceptionResolver brer = new BodyResolvableExceptionResolver();
  4. 【强制】子类的属性不能使用与父类相同的属性名，不论其类型、可见性如何。
- 【推荐】接口和类名使用尽量完整的、准确的单词组合来表达，不要害怕太长。
- 【推荐】接口和类包含的方法、属性、参数、局部变量在命名时尽量不再包含接口和类名，除非不包含接口和类名可能导致多个方法、属性、参数、局部变量之间产生歧义。
- 【推荐】如果模块、接口、类、方法使用了设计模式，在命名时需体现出具体模式。 说明：将设计模式体现在名字中，有利于阅读者快速理解架构设计理念。 
  > 正例： public class OrderFactory;  
  >       public class LoginProxy;  
  >       public class ResourceObserver;
