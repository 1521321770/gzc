package com.gengzc.mq.jar;

import static java.lang.String.format;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.lang.SerializationUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.remoting.RemoteInvocationFailureException;
import org.springframework.remoting.support.DefaultRemoteInvocationFactory;
import org.springframework.remoting.support.RemoteInvocation;
import org.springframework.remoting.support.RemoteInvocationFactory;
import org.springframework.remoting.support.RemoteInvocationResult;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ReturnListener;
import com.rabbitmq.client.ShutdownListener;
import com.rabbitmq.client.ShutdownSignalException;

public class RabbitInvokerClientInterceptor implements MethodInterceptor,
		InitializingBean, ShutdownListener, DisposableBean {

	private final Log log = LogFactory
			.getLog(RabbitInvokerClientInterceptor.class);

	private static final int DEFAULT_TIMEOUT_MS = 30000;
	private static final int DEFAULT_POOL_SIZE = 5;

	private final RemoteInvocationFactory remoteInvocationFactory = new DefaultRemoteInvocationFactory();

	private ConnectionFactory connectionFactory;
	private String exchange;
	private String exchangeTypes;
	private String routingKey;
	private boolean mandatory;
	private boolean immediate;

	private int poolSize = DEFAULT_POOL_SIZE;

	private final BlockingQueue<RabbitRpcClient> rpcClients = new LinkedBlockingQueue<RabbitRpcClient>();

	private int timeoutMs = DEFAULT_TIMEOUT_MS;

	@Override
	public void afterPropertiesSet() throws InterruptedException {

		if (routingKey.contains("#") || routingKey.contains("*")) {
			throw new IllegalArgumentException(
					"Routing key may not contain wildcards.");
		}

		createRpcClients();
	}

	private void createRpcClients() {

		try {
			Channel tmpChannel = connectionFactory.createConnection()
					.createChannel(true);
			tmpChannel.getConnection().addShutdownListener(this);
			tmpChannel.exchangeDeclare(exchange, exchangeTypes.toString());
			for (int i = 0; i < poolSize; i++) {
				Channel channel = connectionFactory.createConnection()
						.createChannel(true);
				final RabbitRpcClient rpcClient = new RabbitRpcClient(channel,
						exchange, routingKey, timeoutMs, mandatory, immediate);
				channel.addReturnListener(new ReturnListener() {
					@SuppressWarnings( { "ThrowableInstanceNeverThrown" })
					public void handleBasicReturn(int replyCode,
							String replyText, String exchange,
							String routingKey, AMQP.BasicProperties properties,
							byte[] body) throws IOException {

						// call handle result here, so uninterruptable cal will
						// be interrupted
						Throwable resultException;
						switch (replyCode) {
						case AMQP.NO_CONSUMERS:
							resultException = new UnroutableException(
									String
											.format(
													"No consumers for message [%s] - [%s] - [%s]",
													SerializationUtils
															.deserialize(body),
													exchange, routingKey));
							break;
						case AMQP.NO_ROUTE:
							resultException = new UnroutableException(
									String
											.format(
													"Unroutable message [%s] - [%s] - [%s]",
													SerializationUtils
															.deserialize(body),
													exchange, routingKey));
							break;
						default:
							resultException = new UnroutableException(
									String
											.format(
													"Message returned [%s] - [%s] - [%s] - [%d] - [%s]",
													SerializationUtils
															.deserialize(body),
													exchange, routingKey,
													replyCode, replyText));

						}
						RemoteInvocationResult remoteInvocationResult = new RemoteInvocationResult(
								resultException);
						rpcClient.getConsumer().handleDelivery(
								null,
								null,
								properties,
								SerializationUtils
										.serialize(remoteInvocationResult));
					}

					@Override
					public void handleReturn(int arg0, String arg1,
							String arg2, String arg3, BasicProperties arg4,
							byte[] arg5) throws IOException {
						// TODO Auto-generated method stub
						
					}
				});
				log
						.info(String
								.format(
										"Started rpc client on exchange [%s(%s)] - routingKey [%s]",
										exchange, exchangeTypes, routingKey));
				rpcClients.add(rpcClient);

			}
		} catch (IOException e) {
			log.warn("Unable to create rpc client", e);
		}
	}

	public Object invoke(MethodInvocation methodInvocation) throws Throwable {
		if (AopUtils.isToStringMethod(methodInvocation.getMethod())) {
			return String.format(
					"Rabbit invoker proxy for exchange [%s] - routingKey [%s]",
					exchange, routingKey);
		}

		RemoteInvocation invocation = createRemoteInvocation(methodInvocation);
		RemoteInvocationResult result = executeRequest(invocation);
		try {
			return recreateRemoteInvocationResult(result);
		} catch (Throwable ex) {
			if (result.hasInvocationTargetException()) {
				throw ex;
			} else {
				throw new RemoteInvocationFailureException(
						String
								.format(
										"Invocation of method [%s] failed in "
												+ "Rabbit invoker remote service at exchange [%s] - routingKey [%s]",
										methodInvocation.getMethod(), exchange,
										routingKey), ex);
			}
		}
	}

	protected RemoteInvocation createRemoteInvocation(
			MethodInvocation methodInvocation) {
		return remoteInvocationFactory.createRemoteInvocation(methodInvocation);
	}

	protected Object recreateRemoteInvocationResult(
			RemoteInvocationResult result) throws Throwable {
		return result.recreate();
	}

	protected RemoteInvocationResult executeRequest(RemoteInvocation invocation)
			throws IOException, TimeoutException, InterruptedException {
		byte[] message = SerializationUtils.serialize(invocation);

		RabbitRpcClient rpcClient = rpcClients.poll(timeoutMs,
				TimeUnit.MILLISECONDS);
		if (rpcClient != null) {

			byte[] response;
			try {
				response = rpcClient.primitiveCall(message);
			} finally {
				rpcClients.put(rpcClient);
			}
			return (RemoteInvocationResult) SerializationUtils
					.deserialize(response);
		}
		throw new TimeoutException(
				"Timed out while waiting for available rpc client");
	}

	@Required
	public void setConnectionFactory(ConnectionFactory connectionFactory) {
		this.connectionFactory = connectionFactory;
	}

	@Required
	public void setRoutingKey(String routingKey) {
		this.routingKey = routingKey;
	}

	@Required
	public void setExchange(String exchange) {
		this.exchange = exchange;
	}

	public void setTimeoutMs(int timeoutMs) {
		this.timeoutMs = timeoutMs;
	}

	public void setMandatory(boolean mandatory) {
		this.mandatory = mandatory;
	}

	public void setImmediate(boolean immediate) {
		this.immediate = immediate;
	}

	@Required
	public void setExchangeTypes(String exchangeTypes) {
		this.exchangeTypes = exchangeTypes;
	}

	public void setPoolSize(int poolSize) {
		this.poolSize = poolSize;
	}

	@Override
	public void shutdownCompleted(ShutdownSignalException cause) {
		if (log.isInfoEnabled()) {
			log.info(String.format("Channel connection lost for reason [%s]",
					cause.getReason()));
			log.info(String.format("Reference [%s]", cause.getReference()));
		}

		if (cause.isInitiatedByApplication()) {
			if (log.isInfoEnabled()) {
				log.info("Sutdown initiated by application");
			}
		} else if (cause.isHardError()) {
			log
					.error("Shutdown is a hard error, trying to restart the RPC clients...");
			clearRpcClients();
			createRpcClients();
		}
	}

	private void clearRpcClients() {
		if (log.isInfoEnabled()) {
			log.info(format("Closing %d rpc clients", rpcClients.size()));
		}

		for (RabbitRpcClient rpcClient : rpcClients) {
			try {
				rpcClient.close();
			} catch (Exception e) {
				log.warn("Error closing rpc client", e);
			}
		}
		rpcClients.clear();

		if (log.isInfoEnabled()) {
			log.info("Rpc clients closed");
		}

	}

	@Override
	public void destroy() throws Exception {
		clearRpcClients();
	}
}

