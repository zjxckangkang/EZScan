package ezscaner.uniview.app.ezscan.eventbus;


import ezscaner.uniview.app.ezscan.constants.PublicConstants;

/**
 * 标准事件消息对象封装
 * 
 * @author 桑龙佳
 * 
 */
public class BaseMessage {

	/**
	 * 事件消息
	 */
	public int event;

	/**
	 * 消息副事件
	 */
	public int subEvent = PublicConstants.DEFAULT_ERROR;

	/**
	 * 事件消息中传递的数据
	 */
	public Object data;

	public BaseMessage() {
		super();
	}

	public BaseMessage(int event, Object data) {
		super();
		this.event = event;
		this.data = data;

	}

	public BaseMessage(int event, int subEvent, Object data) {
		this.event = event;
		this.subEvent = subEvent;
		this.data = data;
	}

	@Override
	public String toString() {
		return "BaseMessage{" +
				"event=" + event +
				", subEvent=" + subEvent +
				", data=" + data +
				'}';
	}
}
