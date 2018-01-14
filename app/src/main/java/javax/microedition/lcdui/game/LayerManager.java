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

package javax.microedition.lcdui.game;

import java.util.Vector;

import javax.microedition.lcdui.Graphics;

public class LayerManager {
	private Vector<Layer> layers;
	private int x, y, width, height;

	public LayerManager() {
		layers = new Vector<>();
		x = y = 0;
		width = height = Integer.MAX_VALUE;
	}

	public void append(Layer layer) {
		synchronized (this) {
			if (layer == null)
				throw new NullPointerException();
			layers.add(layer);
		}
	}

	public void insert(Layer layer, int i) {
		synchronized (this) {
			if (layer == null)
				throw new NullPointerException();
			layers.insertElementAt(layer, i);
		}
	}

	public Layer getLayerAt(int i) {
		return layers.get(i);
	}

	public int getSize() {
		return layers.size();
	}

	public void paint(Graphics g, int x, int y) {
		synchronized (this) {
			if (g == null)
				throw new NullPointerException();
			int clipX = g.getClipX();
			int clipY = g.getClipY();
			int clipW = g.getClipWidth();
			int clipH = g.getClipHeight();
			g.translate(x - this.x, y - this.y);
			g.clipRect(this.x, this.y, width, height);
			for (int i = getSize(); --i >= 0; ) {
				Layer comp = getLayerAt(i);
				if (comp.isVisible()) {
					comp.paint(g);
				}
			}
			g.translate(-x + this.x, -y + this.y);
			g.setClip(clipX, clipY, clipW, clipH);
		}
	}

	public void remove(Layer layer) {
		synchronized (this) {
			if (layer == null)
				throw new NullPointerException();
			layers.remove(layer);
		}
	}

	public void setViewWindow(int x, int y, int width, int height) {
		synchronized (this) {
			if (width < 0 || height < 0)
				throw new IllegalArgumentException();
			this.x = x;
			this.y = y;
			this.width = width;
			this.height = height;
		}
	}

}
