package com.io.socket._4selectorGroup2;

/**
 * @author by catface
 * @date 2021/6/25 11:49 上午
 */
public class MainStarter {

    public static void main(String[] args) {

        WorkGroup workGroup = new WorkGroup(3);
        BossGroup bossGroup = new BossGroup(workGroup);
        bossGroup.bind(9999);

    }
}
