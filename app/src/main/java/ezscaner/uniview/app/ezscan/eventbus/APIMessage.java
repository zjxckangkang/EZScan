package ezscaner.uniview.app.ezscan.eventbus;

import ezscaner.uniview.app.ezscan.constants.APIEventConstants;

/**
 * 调用HTTP或本地数据库API接口后，返回数据事件消息对象封装
 * 
 * @author 桑龙佳
 * 
 */
public class APIMessage extends BaseMessage implements APIEventConstants {

	/**
	 * API调用结果：TRUE成功，FALSE失败
	 */
	public boolean success;

	/**
	 * API调用结果：描述
	 */
	public String description;

	public APIMessage() {
		super();
	}

	public APIMessage(int event, boolean success, String description,
			Object data) {
		super();
		this.event = event;
		this.success = success;
		this.description = description;
		this.data = data;
	}

	public APIMessage(int event, int subEvent, Object data, boolean success, String description) {
		super(event, subEvent, data);
		this.success = success;
		this.description = description;
	}
}
