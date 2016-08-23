package ezscaner.uniview.app.ezscan.eventbus;


import org.greenrobot.eventbus.EventBus;

import ezscaner.uniview.app.ezscan.constants.APIEventConstants;
import ezscaner.uniview.app.ezscan.constants.ViewEventConstants;

/**
 * 调用HTTP或本地数据库API接口后，返回数据事件消息对象封装
 *
 * @author 桑龙佳
 *
 */
public class EventBusUtil  implements APIEventConstants,
		ViewEventConstants {

	private static EventBusUtil mEventBusUtil;
	private static EventBus mEventBus;
	private static final byte[] mByte = new byte[0];

	public static EventBusUtil getInstance() {
		synchronized (mByte) {
			if (mEventBusUtil == null) {
				mEventBusUtil = new EventBusUtil();
			}
			return mEventBusUtil;
		}
	}
	private	EventBusUtil() {
		super();
		mEventBus = EventBus.getDefault();
	}

	public void register(Object context) {
		if (context != null && !mEventBus.isRegistered(context)) {
			mEventBus.register(context);
		}
	}

	public void unregister(Object context) {
		if (context != null && mEventBus.isRegistered(context)) {
			mEventBus.unregister(context);
		}
	}

	public void post(Object message) {
		if (null != message) {
			mEventBus.post(message);
		}
	}

}
