package com.gengzc.mq.jar;

import static java.lang.String.format;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.SerializationUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.remoting.support.RemoteInvocation;
import org.springframework.remoting.support.RemoteInvocationBasedExporter;
import org.springframework.remoting.support.RemoteInvocationResult;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.RpcServer;
import com.rabbitmq.client.ShutdownListener;
import com.rabbitmq.client.ShutdownSignalException;

public class RabbitInvokerServiceExporter extends RemoteInvocationBasedExporter
		implements InitializingBean, DisposableBean, ShutdownListener {

	private final Log log = LogFactory
			.getLog(RabbitInvokerServiceExporter.class);

	private ConnectionFactory connectionFactory;
	private String exchange;
	private String exchangeTypes;
	private String queueName;
	private String routingKey;

	private Object proxy;
	private List<RpcServer> rpcServerPool;
	private int poolsize = 1;

	public void afterPropertiesSet() {

		if (exchangeTypes.equals(ExchangeTypes.FANOUT)) {
			// throw new InvalidRoutingKeyException(String.format(
			// "Exchange type %s not allowed for service exporter",
			// exchangeType));
		}

		proxy = getProxyForService();

		rpcServerPool = new ArrayList<RpcServer>(poolsize);

		startRpcServer();
	}

	private void startRpcServer() {
		try {
			log.info("Creating channel and rpc server");
			Channel tmpChannel = connectionFactory.createConnection()
					.createChannel(true);
			tmpChannel.getConnection().addShutdownListener(this);
			tmpChannel.queueDeclare(queueName, false, false, false, null);
			if (exchange != null) {
				tmpChannel.exchangeDeclare(exchange, exchangeTypes.toString());
				tmpChannel.queueBind(queueName, exchange, routingKey);
			}

			for (int i = 1; i <= poolsize; i++) {
				try {
					Channel channel = connectionFactory.createConnection()
							.createChannel(true);

					log
							.info(String
									.format(
											"Starting rpc server %d on exchange [%s(%s)] - queue [%s] - routingKey [%s]",
											i, exchange, exchangeTypes,
											queueName, routingKey));
					final RpcServer rpcServer = createRpcServer(channel);
					rpcServerPool.add(rpcServer);

					Runnable main = new Runnable() {
						@Override
						public void run() {
							try {
								throw rpcServer.mainloop();
							} catch (IOException e) {
								throw new RuntimeException(e);
							}
						}
					};
					new Thread(main).start();
				} catch (IOException e) {
					log.warn("Unable to create rpc server", e);
				}
			}
		} catch (Exception e) {
			log.error("Unexpected error trying to start rpc servers", e);
		}
	}

	private RpcServer createRpcServer(Channel channel) throws IOException {
		return new RpcServer(channel, queueName) {

			@Override
			public byte[] handleCall(byte[] requestBody,
					AMQP.BasicProperties replyProperties) {

				RemoteInvocation invocation = (RemoteInvocation) SerializationUtils
						.deserialize(requestBody);
				RemoteInvocationResult result = invokeAndCreateResult(
						invocation, proxy);
				return SerializationUtils.serialize(result);

			}
		};
	}

	@Required
	public void setConnectionFactory(ConnectionFactory connectionFactory) {
		this.connectionFactory = connectionFactory;
	}

	@Required
	public void setQueueName(String queueName) {
		this.queueName = queueName;
	}

	public Object getProxy() {
		return proxy;
	}

	@Override
	public void destroy() throws Exception {
		clearRpcServers();
	}

	private void clearRpcServers() {
		if (log.isInfoEnabled()) {
			log.info(format("Closing %d rpc servers", rpcServerPool.size()));
		}

		for (RpcServer rpcServer : rpcServerPool) {
			try {
				rpcServer.terminateMainloop();
				rpcServer.close();
			} catch (Exception e) {
				log.warn("Error termination rpcserver loop", e);
			}
		}
		rpcServerPool.clear();
		if (log.isInfoEnabled()) {
			log.info("Rpc servers closed");
		}

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
					.error("Shutdown is a hard error, trying to restart the RPC server...");
			startRpcServer();
		}
	}

	public void setExchange(String exchange) {
		this.exchange = exchange;
	}

	@Required
	public void setRoutingKey(String routingKey) {
		this.routingKey = routingKey;
	}

	public void setPoolsize(int poolsize) {
		this.poolsize = poolsize;
	}

	@Required
	public void setExchangeTypes(String exchangeTypes) {
		this.exchangeTypes = exchangeTypes;
	}

}
