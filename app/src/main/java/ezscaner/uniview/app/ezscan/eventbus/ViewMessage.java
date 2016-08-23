package ezscaner.uniview.app.ezscan.eventbus;

import ezscaner.uniview.app.ezscan.constants.ViewEventConstants;

/**
 * 更新UI视图元素的事件消息对象封装
 * 
 * @author 桑龙佳
 * 
 */
public class ViewMessage extends BaseMessage implements ViewEventConstants {

	public ViewMessage() {
		super();
	}

	public ViewMessage(int event, Object data) {
		super(event, data);
	}

	public ViewMessage(int event, int subEvent, Object data) {
		super(event, subEvent, data);
	}


}
