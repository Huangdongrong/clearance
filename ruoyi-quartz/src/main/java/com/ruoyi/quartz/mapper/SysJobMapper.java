package com.ruoyi.quartz.mapper;

import com.ruoyi.common.annotation.TargetDS;
import com.ruoyi.quartz.domain.SysJob;
import java.util.List;

/**
 * 调度任务信息 数据层
 *
 * @author ruoyi
 */
@TargetDS
public interface SysJobMapper {

    /**
     * 查询调度任务日志集合
     *
     * @param job 调度信息
     * @return 操作日志集合
     */
    @TargetDS
    public List<SysJob> selectJobList(SysJob job);

    /**
     * 查询所有调度任务
     *
     * @return 调度任务列表
     */
    @TargetDS
    public List<SysJob> selectJobAll();

    /**
     * 通过调度ID查询调度任务信息
     *
     * @param jobId 调度ID
     * @return 角色对象信息
     */
    @TargetDS
    public SysJob selectJobById(Long jobId);

    /**
     * 通过调度ID删除调度任务信息
     *
     * @param jobId 调度ID
     * @return 结果
     */
    @TargetDS
    public int deleteJobById(Long jobId);

    /**
     * 批量删除调度任务信息
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    @TargetDS
    public int deleteJobByIds(Long[] ids);

    /**
     * 修改调度任务信息
     *
     * @param job 调度任务信息
     * @return 结果
     */
    @TargetDS
    public int updateJob(SysJob job);

    /**
     * 新增调度任务信息
     *
     * @param job 调度任务信息
     * @return 结果
     */
    @TargetDS
    public int insertJob(SysJob job);
}
