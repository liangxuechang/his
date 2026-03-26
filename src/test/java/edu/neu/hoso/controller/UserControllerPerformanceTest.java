package edu.neu.hoso.controller;

import com.alibaba.fastjson.JSON;
import edu.neu.hoso.model.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

/**
 * UserController 性能测试类
 * 包含并发测试、压力测试、稳定性测试
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerPerformanceTest {

    @Autowired
    private MockMvc mockMvc;

    private MockHttpSession session;

    // 测试配置
    private static final int WARM_UP_ITERATIONS = 100;
    private static final int[] CONCURRENT_USERS = {50, 100, 200};
    private static final int TEST_DURATION_SECONDS = 30;

    @Before
    public void setUp() {
        // 模拟登录状态
        session = new MockHttpSession();
        User user = new User();
        user.setUserId(1);
        user.setUserLoginname("admin");
        user.setUserName("管理员");
        session.setAttribute("user", user);
    }

    /**
     * 预热 - 执行一定次数的请求以预热JVM
     */
    @Test
    public void warmUp() throws Exception {
        System.out.println("开始预热...");
        for (int i = 0; i < WARM_UP_ITERATIONS; i++) {
            mockMvc.perform(get("/user/getAllUser")
                    .session(session));
        }
        System.out.println("预热完成");
    }

    /**
     * TC-PERF-001: 登录接口并发性能测试
     */
    @Test
    public void testLoginConcurrentPerformance() throws Exception {
        System.out.println("\n========== 登录接口并发性能测试 ==========");

        for (int concurrentUsers : CONCURRENT_USERS) {
            System.out.println("\n--- 并发用户数: " + concurrentUsers + " ---");

            ExecutorService executor = Executors.newFixedThreadPool(concurrentUsers);
            CountDownLatch latch = new CountDownLatch(concurrentUsers);
            List<Future<PerformanceResult>> futures = new ArrayList<>();

            long testStartTime = System.currentTimeMillis();

            for (int i = 0; i < concurrentUsers; i++) {
                final int userIndex = i;
                Future<PerformanceResult> future = executor.submit(() -> {
                    long threadStartTime = System.currentTimeMillis();
                    int successCount = 0;
                    int failCount = 0;
                    long totalResponseTime = 0;
                    long minResponseTime = Long.MAX_VALUE;
                    long maxResponseTime = 0;

                    try {
                        while (System.currentTimeMillis() - testStartTime < TEST_DURATION_SECONDS * 1000) {
                            long requestStart = System.currentTimeMillis();
                            try {
                                MvcResult result = mockMvc.perform(get("/login/LoginUser")
                                        .param("userLoginName", "admin")
                                        .param("password", "123456"))
                                        .andReturn();

                                long responseTime = System.currentTimeMillis() - requestStart;
                                totalResponseTime += responseTime;
                                minResponseTime = Math.min(minResponseTime, responseTime);
                                maxResponseTime = Math.max(maxResponseTime, responseTime);

                                if (result.getResponse().getStatus() == 200) {
                                    successCount++;
                                } else {
                                    failCount++;
                                }
                            } catch (Exception e) {
                                failCount++;
                            }
                        }
                    } finally {
                        latch.countDown();
                    }

                    long threadDuration = System.currentTimeMillis() - threadStartTime;
                    return new PerformanceResult(successCount, failCount, totalResponseTime,
                            minResponseTime == Long.MAX_VALUE ? 0 : minResponseTime,
                            maxResponseTime, threadDuration);
                });
                futures.add(future);
            }

            latch.await();
            executor.shutdown();

            // 汇总结果
            int totalSuccess = 0;
            int totalFail = 0;
            long totalResponseTime = 0;
            long minResponseTime = Long.MAX_VALUE;
            long maxResponseTime = 0;

            for (Future<PerformanceResult> future : futures) {
                PerformanceResult result = future.get();
                totalSuccess += result.successCount;
                totalFail += result.failCount;
                totalResponseTime += result.totalResponseTime;
                minResponseTime = Math.min(minResponseTime, result.minResponseTime);
                maxResponseTime = Math.max(maxResponseTime, result.maxResponseTime);
            }

            int totalRequests = totalSuccess + totalFail;
            double avgResponseTime = totalSuccess > 0 ? (double) totalResponseTime / totalSuccess : 0;
            double throughput = totalRequests / (double) TEST_DURATION_SECONDS;
            double errorRate = totalRequests > 0 ? (double) totalFail / totalRequests * 100 : 0;

            System.out.println("总请求数: " + totalRequests);
            System.out.println("成功请求: " + totalSuccess);
            System.out.println("失败请求: " + totalFail);
            System.out.println("平均响应时间: " + String.format("%.2f", avgResponseTime) + " ms");
            System.out.println("最小响应时间: " + minResponseTime + " ms");
            System.out.println("最大响应时间: " + maxResponseTime + " ms");
            System.out.println("吞吐量(TPS): " + String.format("%.2f", throughput));
            System.out.println("错误率: " + String.format("%.2f", errorRate) + "%");
        }
    }

    /**
     * TC-PERF-002: 新增用户接口并发性能测试
     */
    @Test
    @Transactional
    public void testInsertUserConcurrentPerformance() throws Exception {
        System.out.println("\n========== 新增用户接口并发性能测试 ==========");

        int[] insertConcurrentUsers = {50, 100};

        for (int concurrentUsers : insertConcurrentUsers) {
            System.out.println("\n--- 并发用户数: " + concurrentUsers + " ---");

            ExecutorService executor = Executors.newFixedThreadPool(concurrentUsers);
            CountDownLatch latch = new CountDownLatch(concurrentUsers);
            List<Future<PerformanceResult>> futures = new ArrayList<>();

            long testStartTime = System.currentTimeMillis();

            for (int i = 0; i < concurrentUsers; i++) {
                final int userIndex = i;
                Future<PerformanceResult> future = executor.submit(() -> {
                    long threadStartTime = System.currentTimeMillis();
                    int successCount = 0;
                    int failCount = 0;
                    long totalResponseTime = 0;
                    long minResponseTime = Long.MAX_VALUE;
                    long maxResponseTime = 0;
                    int requestCount = 0;

                    try {
                        while (System.currentTimeMillis() - testStartTime < TEST_DURATION_SECONDS * 1000) {
                            User user = new User();
                            user.setUserLoginname("perftest" + userIndex + "_" + (requestCount++) + "_" + System.currentTimeMillis());
                            user.setUserPassword("123456");
                            user.setRoleId(2);
                            user.setUserName("性能测试用户");
                            user.setDepartmentId(1);

                            long requestStart = System.currentTimeMillis();
                            try {
                                MvcResult result = mockMvc.perform(post("/user/insert")
                                        .session(session)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(JSON.toJSONString(user)))
                                        .andReturn();

                                long responseTime = System.currentTimeMillis() - requestStart;
                                totalResponseTime += responseTime;
                                minResponseTime = Math.min(minResponseTime, responseTime);
                                maxResponseTime = Math.max(maxResponseTime, responseTime);

                                if (result.getResponse().getStatus() == 200) {
                                    successCount++;
                                } else {
                                    failCount++;
                                }
                            } catch (Exception e) {
                                failCount++;
                            }
                        }
                    } finally {
                        latch.countDown();
                    }

                    long threadDuration = System.currentTimeMillis() - threadStartTime;
                    return new PerformanceResult(successCount, failCount, totalResponseTime,
                            minResponseTime == Long.MAX_VALUE ? 0 : minResponseTime,
                            maxResponseTime, threadDuration);
                });
                futures.add(future);
            }

            latch.await();
            executor.shutdown();

            // 汇总结果
            int totalSuccess = 0;
            int totalFail = 0;
            long totalResponseTime = 0;
            long minResponseTime = Long.MAX_VALUE;
            long maxResponseTime = 0;

            for (Future<PerformanceResult> future : futures) {
                PerformanceResult result = future.get();
                totalSuccess += result.successCount;
                totalFail += result.failCount;
                totalResponseTime += result.totalResponseTime;
                minResponseTime = Math.min(minResponseTime, result.minResponseTime);
                maxResponseTime = Math.max(maxResponseTime, result.maxResponseTime);
            }

            int totalRequests = totalSuccess + totalFail;
            double avgResponseTime = totalSuccess > 0 ? (double) totalResponseTime / totalSuccess : 0;
            double throughput = totalRequests / (double) TEST_DURATION_SECONDS;
            double errorRate = totalRequests > 0 ? (double) totalFail / totalRequests * 100 : 0;

            System.out.println("总请求数: " + totalRequests);
            System.out.println("成功请求: " + totalSuccess);
            System.out.println("失败请求: " + totalFail);
            System.out.println("平均响应时间: " + String.format("%.2f", avgResponseTime) + " ms");
            System.out.println("最小响应时间: " + minResponseTime + " ms");
            System.out.println("最大响应时间: " + maxResponseTime + " ms");
            System.out.println("吞吐量(TPS): " + String.format("%.2f", throughput));
            System.out.println("错误率: " + String.format("%.2f", errorRate) + "%");
        }
    }

    /**
     * TC-PERF-003: 删除用户接口并发性能测试
     */
    @Test
    @Transactional
    public void testDeleteUserConcurrentPerformance() throws Exception {
        System.out.println("\n========== 删除用户接口并发性能测试 ==========");

        // 预创建一批测试用户
        List<Integer> userIds = new ArrayList<>();
        int preCreateCount = 500;

        System.out.println("预创建 " + preCreateCount + " 个测试用户...");
        for (int i = 0; i < preCreateCount; i++) {
            User user = new User();
            user.setUserLoginname("deleteperf" + i + "_" + System.currentTimeMillis());
            user.setUserPassword("123456");
            user.setRoleId(2);
            user.setUserName("删除性能测试用户");
            user.setDepartmentId(1);

            try {
                MvcResult result = mockMvc.perform(post("/user/insert")
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSON.toJSONString(user)))
                        .andReturn();

                if (result.getResponse().getStatus() == 200) {
                    String content = result.getResponse().getContentAsString();
                    User insertedUser = JSON.parseObject(
                            JSON.parseObject(content).getString("data"), User.class);
                    if (insertedUser != null) {
                        userIds.add(insertedUser.getUserId());
                    }
                }
            } catch (Exception e) {
                // 忽略插入失败
            }
        }

        System.out.println("成功创建 " + userIds.size() + " 个测试用户");

        int concurrentUsers = 50;
        System.out.println("\n--- 并发用户数: " + concurrentUsers + " ---");

        ExecutorService executor = Executors.newFixedThreadPool(concurrentUsers);
        CountDownLatch latch = new CountDownLatch(concurrentUsers);
        List<Future<PerformanceResult>> futures = new ArrayList<>();

        long testStartTime = System.currentTimeMillis();

        for (int i = 0; i < concurrentUsers; i++) {
            final int userIndex = i;
            Future<PerformanceResult> future = executor.submit(() -> {
                long threadStartTime = System.currentTimeMillis();
                int successCount = 0;
                int failCount = 0;
                long totalResponseTime = 0;
                long minResponseTime = Long.MAX_VALUE;
                long maxResponseTime = 0;
                int requestCount = 0;

                try {
                    while (System.currentTimeMillis() - testStartTime < TEST_DURATION_SECONDS * 1000
                            && requestCount < userIds.size() / concurrentUsers) {
                        int idIndex = userIndex * (userIds.size() / concurrentUsers) + requestCount;
                        if (idIndex >= userIds.size()) {
                            break;
                        }

                        long requestStart = System.currentTimeMillis();
                        try {
                            MvcResult result = mockMvc.perform(get("/user/delete")
                                    .session(session)
                                    .param("id", String.valueOf(userIds.get(idIndex))))
                                    .andReturn();

                            long responseTime = System.currentTimeMillis() - requestStart;
                            totalResponseTime += responseTime;
                            minResponseTime = Math.min(minResponseTime, responseTime);
                            maxResponseTime = Math.max(maxResponseTime, responseTime);

                            if (result.getResponse().getStatus() == 200) {
                                successCount++;
                            } else {
                                failCount++;
                            }
                        } catch (Exception e) {
                            failCount++;
                        }
                        requestCount++;
                    }
                } finally {
                    latch.countDown();
                }

                long threadDuration = System.currentTimeMillis() - threadStartTime;
                return new PerformanceResult(successCount, failCount, totalResponseTime,
                        minResponseTime == Long.MAX_VALUE ? 0 : minResponseTime,
                        maxResponseTime, threadDuration);
            });
            futures.add(future);
        }

        latch.await();
        executor.shutdown();

        // 汇总结果
        int totalSuccess = 0;
        int totalFail = 0;
        long totalResponseTime = 0;
        long minResponseTime = Long.MAX_VALUE;
        long maxResponseTime = 0;

        for (Future<PerformanceResult> future : futures) {
            PerformanceResult result = future.get();
            totalSuccess += result.successCount;
            totalFail += result.failCount;
            totalResponseTime += result.totalResponseTime;
            minResponseTime = Math.min(minResponseTime, result.minResponseTime);
            maxResponseTime = Math.max(maxResponseTime, result.maxResponseTime);
        }

        int totalRequests = totalSuccess + totalFail;
        double avgResponseTime = totalSuccess > 0 ? (double) totalResponseTime / totalSuccess : 0;
        double throughput = totalRequests / (double) TEST_DURATION_SECONDS;
        double errorRate = totalRequests > 0 ? (double) totalFail / totalRequests * 100 : 0;

        System.out.println("总请求数: " + totalRequests);
        System.out.println("成功请求: " + totalSuccess);
        System.out.println("失败请求: " + totalFail);
        System.out.println("平均响应时间: " + String.format("%.2f", avgResponseTime) + " ms");
        System.out.println("最小响应时间: " + minResponseTime + " ms");
        System.out.println("最大响应时间: " + maxResponseTime + " ms");
        System.out.println("吞吐量(TPS): " + String.format("%.2f", throughput));
        System.out.println("错误率: " + String.format("%.2f", errorRate) + "%");
    }

    /**
     * TC-PERF-004: 接口稳定性测试（长时间运行）
     */
    @Test
    @Transactional
    public void testStability() throws Exception {
        System.out.println("\n========== 接口稳定性测试（5分钟） ==========");

        int concurrentUsers = 50;
        int stabilityDurationSeconds = 300; // 5分钟

        ExecutorService executor = Executors.newFixedThreadPool(concurrentUsers);
        CountDownLatch latch = new CountDownLatch(concurrentUsers);
        List<Future<StabilityResult>> futures = new ArrayList<>();

        long testStartTime = System.currentTimeMillis();

        for (int i = 0; i < concurrentUsers; i++) {
            final int userIndex = i;
            Future<StabilityResult> future = executor.submit(() -> {
                int loginCount = 0;
                int insertCount = 0;
                int deleteCount = 0;
                int queryCount = 0;
                int errorCount = 0;

                try {
                    while (System.currentTimeMillis() - testStartTime < stabilityDurationSeconds * 1000) {
                        try {
                            // 随机选择操作类型
                            int operation = (int) (Math.random() * 4);

                            switch (operation) {
                                case 0: // 登录
                                    mockMvc.perform(get("/login/LoginUser")
                                            .param("userLoginName", "admin")
                                            .param("password", "123456"));
                                    loginCount++;
                                    break;

                                case 1: // 新增用户
                                    User user = new User();
                                    user.setUserLoginname("stable" + userIndex + "_" + System.currentTimeMillis());
                                    user.setUserPassword("123456");
                                    user.setRoleId(2);
                                    user.setUserName("稳定性测试用户");
                                    user.setDepartmentId(1);
                                    mockMvc.perform(post("/user/insert")
                                            .session(session)
                                            .contentType(MediaType.APPLICATION_JSON)
                                            .content(JSON.toJSONString(user)));
                                    insertCount++;
                                    break;

                                case 2: // 删除用户（使用不存在的ID，避免影响数据）
                                    mockMvc.perform(get("/user/delete")
                                            .session(session)
                                            .param("id", "999999"));
                                    deleteCount++;
                                    break;

                                case 3: // 查询
                                    mockMvc.perform(get("/user/getAllUser")
                                            .session(session));
                                    queryCount++;
                                    break;
                            }
                        } catch (Exception e) {
                            errorCount++;
                        }

                        // 短暂休息，避免请求过于密集
                        Thread.sleep(10);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    latch.countDown();
                }

                return new StabilityResult(loginCount, insertCount, deleteCount, queryCount, errorCount);
            });
            futures.add(future);
        }

        latch.await();
        executor.shutdown();

        // 汇总结果
        int totalLogin = 0;
        int totalInsert = 0;
        int totalDelete = 0;
        int totalQuery = 0;
        int totalError = 0;

        for (Future<StabilityResult> future : futures) {
            StabilityResult result = future.get();
            totalLogin += result.loginCount;
            totalInsert += result.insertCount;
            totalDelete += result.deleteCount;
            totalQuery += result.queryCount;
            totalError += result.errorCount;
        }

        int totalRequests = totalLogin + totalInsert + totalDelete + totalQuery;
        double errorRate = totalRequests > 0 ? (double) totalError / totalRequests * 100 : 0;

        System.out.println("\n稳定性测试结果:");
        System.out.println("总请求数: " + totalRequests);
        System.out.println("  - 登录请求: " + totalLogin);
        System.out.println("  - 新增请求: " + totalInsert);
        System.out.println("  - 删除请求: " + totalDelete);
        System.out.println("  - 查询请求: " + totalQuery);
        System.out.println("错误数: " + totalError);
        System.out.println("错误率: " + String.format("%.4f", errorRate) + "%");
        System.out.println("平均TPS: " + String.format("%.2f", totalRequests / (double) stabilityDurationSeconds));
    }

    /**
     * 性能测试结果类
     */
    private static class PerformanceResult {
        final int successCount;
        final int failCount;
        final long totalResponseTime;
        final long minResponseTime;
        final long maxResponseTime;
        final long duration;

        PerformanceResult(int successCount, int failCount, long totalResponseTime,
                         long minResponseTime, long maxResponseTime, long duration) {
            this.successCount = successCount;
            this.failCount = failCount;
            this.totalResponseTime = totalResponseTime;
            this.minResponseTime = minResponseTime;
            this.maxResponseTime = maxResponseTime;
            this.duration = duration;
        }
    }

    /**
     * 稳定性测试结果类
     */
    private static class StabilityResult {
        final int loginCount;
        final int insertCount;
        final int deleteCount;
        final int queryCount;
        final int errorCount;

        StabilityResult(int loginCount, int insertCount, int deleteCount, int queryCount, int errorCount) {
            this.loginCount = loginCount;
            this.insertCount = insertCount;
            this.deleteCount = deleteCount;
            this.queryCount = queryCount;
            this.errorCount = errorCount;
        }
    }
}
