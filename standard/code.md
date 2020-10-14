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
1. 【强制】命名必须准确、清晰易懂、无歧义、常用非生僻、与实际含义吻合，让读者能见名知意。
2. 【强制】使用英文单词及其组合，不得随意使用拼音、拼音缩写、中文。
  > 例外1：对外宣传用的网页URL地址中可使用拼音，以利于用户记忆  
  > 例外2：公司、品牌、产品名称、地名等本身即为拼音的，可直接引用，如：alibaba/taobao/hangzhou
3. 【强制】避免使用任何语言的种族歧视性词语
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
1. 【强制】所有文本的缩进一律使用4位空格，所有Tab均替换为4位空格
2. 【强制】所有文本行的最大宽度均为120个字符，设置代码格式化程序以该数值为基准进行自动格式化
3. 【强制】所有文本文件的字符编码格式统一为UTF-8
4. 【推荐】所有文本文件的最后以一个空行结束，以便于SCM工具辨别文件结尾

## 二、Java规约
1. 【强制】所有命名均不能以_和$开头，也不能以_和$结尾。
### 2.1 工程结构
1. 【强制】Java工程均通过maven创建，其基本目录结构和命名遵从maven的规范和习惯。
2. 【强制】一个典型的maven工程，由一个pom类型的顶级父工程，和多个子工程组成。
对于分布式微服务架构的工程，这些子工程为微服务子工程，也是pom类型的maven工程，其下包含各层级的子工程。
所有工程的命名都只使用小写英文字母，单词之间用-分隔。

    层级子工程|命名方式|范例|说明
    ---|---|---|---
    api|[顶级工程名]-[微服务名]-api|tnxsample-admin-api|微服务对外提供服务的接口定义模块
    model|[顶级工程名]-[微服务名]-model|tnxsample-admin-model|微服务的领域模型定义模块
    repo|[顶级工程名]-[微服务名]-repo|tnxsample-admin-repo|微服务内部的数据持久化模块
    service|[顶级工程名]-[微服务名]-service|tnxsample-admin-service|微服务内部的业务接口定义和逻辑实现模块
    web|[顶级工程名]-[微服务名]-web|tnxsample-admin-web|微服务的视图模块
    
  > 说明：层级子工程之间的依赖关系详见样例工程 [tnxsample](../../tnxsample)
### 2.2 包