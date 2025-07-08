package cn.master.luna.util;

import cn.master.luna.constants.ApplicationNumScope;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.redisson.Redisson;
import org.redisson.api.RIdGenerator;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author Created by 11's papa on 2025/7/8
 */
@Component
public class NumGenerator {
    private static final long INIT = 100001L; // 代表从100001开始，各种domain的 num
    private static final long LIMIT = 1;

    private static Redisson redisson;
    private static final List<ApplicationNumScope> SUB_NUM = List.of(ApplicationNumScope.API_TEST_CASE, ApplicationNumScope.API_MOCK, ApplicationNumScope.TEST_PLAN_FUNCTION_CASE, ApplicationNumScope.TEST_PLAN_API_CASE, ApplicationNumScope.TEST_PLAN_API_SCENARIO);
    @Resource
    public void setRedisson(Redisson redisson) {
        NumGenerator.redisson = redisson;
    }

    public static long nextNum(String prefix, ApplicationNumScope scope) {
        RIdGenerator idGenerator = redisson.getIdGenerator(prefix + "_" + scope.name());
        if (SUB_NUM.contains(scope)) {
            if (!idGenerator.isExists()) {
                idGenerator.tryInit(1, LIMIT);
            }
            return Long.parseLong(prefix.split("_")[1] + StringUtils.leftPad(String.valueOf(idGenerator.nextId()), 3, "0"));
        } else {
            // 每次都尝试初始化，容量为1，只有一个线程可以初始化成功
            if (!idGenerator.isExists()) {
                idGenerator.tryInit(INIT, LIMIT);
            }
            return idGenerator.nextId();
        }
    }
}
