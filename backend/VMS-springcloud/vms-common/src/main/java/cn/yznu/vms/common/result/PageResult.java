package cn.yznu.vms.common.result;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.Data;

import java.util.List;

/**
 * 分页返回结果包装类
 * 统一分页接口返回格式，便于前端处理
 *
 * @param <T> 数据类型
 */
@Data
public class PageResult<T> {

    /**
     * 分页数据列表
     */
    private List<T> pageData;

    /**
     * 总记录数
     */
    private Long total;

    /**
     * 当前页码
     */
    private Long pageNo;

    /**
     * 每页条数
     */
    private Long pageSize;

    /**
     * 总页数
     */
    private Long pages;

    /**
     * 从 MyBatis-Plus IPage 创建分页结果
     *
     * @param page IPage 分页对象
     * @return PageResult
     */
    public static <T> PageResult<T> of(IPage<T> page) {
        PageResult<T> result = new PageResult<>();
        result.setPageData(page.getRecords());
        result.setTotal(page.getTotal());
        result.setPageNo(page.getCurrent());
        result.setPageSize(page.getSize());
        result.setPages(page.getPages());
        return result;
    }

    /**
     * 从列表创建分页结果（用于内存分页场景）
     *
     * @param list  数据列表
     * @param total 总数
     * @return PageResult
     */
    public static <T> PageResult<T> of(List<T> list, long total) {
        PageResult<T> result = new PageResult<>();
        result.setPageData(list);
        result.setTotal(total);
        return result;
    }
}
