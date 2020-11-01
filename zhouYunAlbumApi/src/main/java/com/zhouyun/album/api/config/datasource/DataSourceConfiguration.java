package com.zhouyun.album.api.config.datasource;



import com.zhouyun.album.api.configuration.SpringBootDataSourceProperties;
import com.zhouyun.core.dataSource.DataSourceFactory;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.aop.Advisor;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.interceptor.*;

import javax.sql.DataSource;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@MapperScan(
        basePackages = {
                "com.zhouyun.album.mapper"
        },
        sqlSessionFactoryRef = "sqlSessionFactory")
@Configuration
@EnableCaching
@EnableConfigurationProperties({SpringBootDataSourceProperties.class})
public class DataSourceConfiguration {
    private static final int TX_METHOD_TIMEOUT = 60;
    private static final String AOP_POINTCUT_EXPRESSION = "execution (* com.zhouyun.album.service.biz..*(..))";

    private final SpringBootDataSourceProperties springBootDataSourceProperties;

    @Autowired
    public DataSourceConfiguration(SpringBootDataSourceProperties springBootDataSourceProperties) {
        this.springBootDataSourceProperties = springBootDataSourceProperties;

    }

    /**
     * 实例DataSource
     *
     * @return
     */
    @Bean(name = "dataSource", initMethod = "init", destroyMethod = "close")
    @Primary
    public DataSource rdsDataSource() {
        return DataSourceFactory.createDataSource(this.springBootDataSourceProperties);
    }

    /**
     * 事务管理
     *
     * @return
     */
    @Bean(name = "tx_admin")
    public DataSourceTransactionManager rdsTransactionManager() {
        return new DataSourceTransactionManager(rdsDataSource());
    }

    /**
     * sql链接工厂
     *
     * @param rdsDataSource
     * @return
     * @throws Exception
     */
    @Bean(name = "sqlSessionFactory")
    public SqlSessionFactory sqlSessionFactory(@Qualifier("dataSource") DataSource rdsDataSource) throws Exception {
        return DataSourceFactory.createSqlSessionFactoryBean(rdsDataSource, new String[]{"classpath:mapper/**/*.xml"}).getObject();
    }

    /**
     * 统一配置事务
     *
     * @param transactionManager
     * @return
     */
    @Bean
    public TransactionInterceptor txAdvice(@Qualifier("tx_admin") PlatformTransactionManager transactionManager) {
        NameMatchTransactionAttributeSource source = new NameMatchTransactionAttributeSource();
        /*只读事务，不做更新操作*/
        RuleBasedTransactionAttribute readOnlyTx = new RuleBasedTransactionAttribute();
        readOnlyTx.setReadOnly(true);
        readOnlyTx.setPropagationBehavior(TransactionDefinition.PROPAGATION_NOT_SUPPORTED);
        /*当前存在事务就使用当前事务，当前不存在事务就创建一个新的事务*/
        RuleBasedTransactionAttribute requiredTx = new RuleBasedTransactionAttribute();
        requiredTx.setRollbackRules(Collections.singletonList(new RollbackRuleAttribute(Exception.class)));
        requiredTx.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        requiredTx.setTimeout(TX_METHOD_TIMEOUT);
        Map<String, TransactionAttribute> txMap = new HashMap<>(14);

        txMap.put("add*", requiredTx);
        txMap.put("save*", requiredTx);
        txMap.put("insert*", requiredTx);
        txMap.put("create*", requiredTx);
        txMap.put("update*", requiredTx);
        txMap.put("modify*", requiredTx);
        txMap.put("delete*", requiredTx);
        txMap.put("remove*", requiredTx);
        txMap.put("disable*", requiredTx);
        txMap.put("audit*", requiredTx);


        txMap.put("get*", readOnlyTx);
        txMap.put("find*", readOnlyTx);
        txMap.put("list*", readOnlyTx);
        txMap.put("page*", readOnlyTx);
        txMap.put("query*", readOnlyTx);
        txMap.put("select*", readOnlyTx);

        source.setNameMap(txMap);
        return new TransactionInterceptor(transactionManager, source);
    }

    @Bean
    public Advisor txAdviceAdvisor(@Qualifier("tx_admin") PlatformTransactionManager transactionManager) {
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        pointcut.setExpression(AOP_POINTCUT_EXPRESSION);
        return new DefaultPointcutAdvisor(pointcut, txAdvice(transactionManager));
    }

}
