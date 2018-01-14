/*
 * Copyright 2017 Nikita Shakarun
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package javax.microedition.sensor;

import javax.microedition.io.Connection;

public interface SensorConnection extends Connection {
	public static final int STATE_CLOSED = 4;
	public static final int STATE_LISTENING = 2;
	public static final int STATE_OPENED = 1;

	Channel getChannel(ChannelInfo channelInfo);

	Data[] getData(int i);

	Data[] getData(int i, long j, boolean z, boolean z2, boolean z3);

	int[] getErrorCodes();

	String getErrorText(int i);

	SensorInfo getSensorInfo();

	int getState();

	void removeDataListener();

	void setDataListener(DataListener dataListener, int i);

	void setDataListener(DataListener dataListener, int i, long j, boolean z, boolean z2, boolean z3);
}
