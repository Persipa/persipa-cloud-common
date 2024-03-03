package site.persipa.common.entity.constant.mq;

/**
 * @author persipa
 */
public class RabbitConstant {

    /**
     * rabbitmq 延迟交换机类型名
     */
    public static final String RABBITMQ_DELAYED_EXCHANGE_TYPE = "x-delayed-message";

    /**
     * rabbitmq 延迟交换机附加额外头部
     */
    public static final String RABBITMQ_DELAYED_EXCHANGE_EXTRA_HEADER = "x-delayed-type";

    /**
     * rabbitmq 延迟消息发送的头部
     */
    public static final String RABBITMQ_DELAYED_MESSAGE_HEADER = "x-delay";

    /**
     * 默认的RabbitMQ 队列主题名称
     */
    public static final String RABBITMQ_DEFAULT_TOPIC = "rabbitmqDefaultTopic";

    /**
     * 默认的RabbitMQ 交换机名称
     */
    public static final String RABBITMQ_DEFAULT_DIRECT_EXCHANGE = "rabbitmqDefaultDirectExchange";

    /**
     * 默认的RabbitMQ 交换机和队列绑定的匹配键
     */
    public static final String RABBIT_DEFAULT_DIRECT_ROUTING = "rabbitmqDefaultDirectRouting";
}
