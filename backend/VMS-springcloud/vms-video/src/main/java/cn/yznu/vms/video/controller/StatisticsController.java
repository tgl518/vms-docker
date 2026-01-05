package cn.yznu.vms.video.controller;

import cn.yznu.vms.common.result.Result;
import cn.yznu.vms.video.service.StatisticsService;
import cn.yznu.vms.video.vo.StatisticsVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 统计数据接口
 */
@Tag(name = "统计接口")
@RestController
@RequestMapping("/statistics")
@RequiredArgsConstructor
public class StatisticsController {

    private final StatisticsService statisticsService;

    @Operation(summary = "获取仪表盘统计数据")
    @GetMapping("/dashboard")
    public Result<StatisticsVO> getDashboard() {
        return Result.success(statisticsService.getDashboardStats());
    }
}
