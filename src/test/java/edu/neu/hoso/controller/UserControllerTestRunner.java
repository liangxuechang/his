package edu.neu.hoso.controller;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

/**
 * UserController 测试运行器
 * 用于批量运行所有测试类并生成测试报告
 */
public class UserControllerTestRunner {

    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("    UserController 测试套件开始执行     ");
        System.out.println("========================================\n");

        // 运行所有测试类
        Result result = JUnitCore.runClasses(
                UserControllerTest.class,
                UserControllerPerformanceTest.class
        );

        // 打印测试结果
        printTestResults(result);
    }

    private static void printTestResults(Result result) {
        System.out.println("\n========================================");
        System.out.println("           测试执行结果汇总             ");
        System.out.println("========================================");
        System.out.println("运行时间: " + result.getRunTime() + " ms");
        System.out.println("运行测试: " + result.getRunCount());
        System.out.println("通过测试: " + (result.getRunCount() - result.getFailureCount() - result.getIgnoreCount()));
        System.out.println("失败测试: " + result.getFailureCount());
        System.out.println("忽略测试: " + result.getIgnoreCount());
        System.out.println("成功率: " + String.format("%.2f%%", 
                (result.getRunCount() - result.getFailureCount() - result.getIgnoreCount()) * 100.0 / result.getRunCount()));

        if (result.getFailureCount() > 0) {
            System.out.println("\n----------------------------------------");
            System.out.println("           失败的测试详情               ");
            System.out.println("----------------------------------------");
            for (Failure failure : result.getFailures()) {
                System.out.println("\n测试方法: " + failure.getTestHeader());
                System.out.println("失败原因: " + failure.getMessage());
                System.out.println("异常信息: " + failure.getTrace());
            }
        }

        System.out.println("\n========================================");
        System.out.println("           测试执行" + (result.wasSuccessful() ? "成功" : "失败") + "               ");
        System.out.println("========================================");

        // 返回退出码
        System.exit(result.wasSuccessful() ? 0 : 1);
    }
}
