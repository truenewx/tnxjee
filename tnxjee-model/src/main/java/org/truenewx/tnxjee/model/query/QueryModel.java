package org.truenewx.tnxjee.model.query;

import org.truenewx.tnxjee.model.Model;

/**
 * 查询模型，用于在执行查询型（不会改动实体数据）操作时传递查询条件
 *
 * @author jianglei
 */
public interface QueryModel extends Model {

    /**
     * @return 分页信息
     */
    Paging getPaging();

    /**
     * @return 是否获取总数
     */
    boolean isTotalable();

    /**
     * @return 是否获取记录清单
     */
    boolean isListable();

}
