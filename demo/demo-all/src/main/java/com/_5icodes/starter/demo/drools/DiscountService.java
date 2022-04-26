package com._5icodes.starter.demo.drools;

import lombok.SneakyThrows;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.impl.KnowledgeBaseFactory;
import org.kie.api.KieBase;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.utils.KieHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Service
public class DiscountService {
    @Autowired
    private KieContainer kieContainer;

    public void applyDiscount(Sale sale) {
        KieSession kieSession = kieContainer.newKieSession();
        //插入事实对象
        kieSession.insert(sale);
        kieSession.fireAllRules();
        kieSession.dispose();
    }

    @Autowired
    private KieBase kieBase;

    public void applyDiscount2(Sale sale) {
        KieSession kieSession = kieBase.newKieSession();
        //插入事实对象
        kieSession.insert(sale);
        kieSession.fireAllRules();
        kieSession.dispose();
    }

    /**
     * 由从数据库里面读出的drl构建KieSession
     */
    private void build() {
        String rule = "package com.example;\r\n";
        rule += "import com.example.service.*;\r\n";
        rule += "rule \"rule1\"\r\n";
        rule += "when\r\n";
        rule += "\t Message(status == \"1\")";
        rule += "\r\nthen\r\n";
        rule += "\tSystem.out.println(\"hello\");";
        rule += "end\r\n";
        method1(rule);
        method2(rule);
    }

    /**
     * 方式一：使用KieHelper封装类
     * drools提供了封装好的KieHelper类，通过以下几行代码实现了KieSession的构建
     * 其中rule为字符串，可以从数据库读取。
     * 使用这种方式的优点在于代码简单，重要的代码只有三行，缺点是无法对编译规则错误时的提示进行个性化的处理。推荐使用以下不借助封装类的实现方式。
     *
     * @param drl
     * @return
     */
    private KieSession method1(String drl) {
        KieHelper helper = new KieHelper();
        helper.addContent(drl, ResourceType.DRL);
        return helper.build().newKieSession();
    }

    /**
     * 方式二：不使用KieHelper封装类
     * 可以抽象异常，将错误信息抛到上层，最终通过接口返回给前端
     *
     * @param drl
     * @return
     */
    @SneakyThrows
    private KieSession method2(String drl) {
        KnowledgeBuilder kb = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kb.add(ResourceFactory.newByteArrayResource(drl.getBytes(StandardCharsets.UTF_8)), ResourceType.DRL);
        if (kb.hasErrors()) {
            String errorMessage = kb.getErrors().toString();
            System.out.println("规则语法异常---\n" + errorMessage);
            return null;
        }
        InternalKnowledgeBase kBase = KnowledgeBaseFactory.newKnowledgeBase();
        kBase.addPackages(kb.getKnowledgePackages());
        return kBase.newKieSession();
    }
}