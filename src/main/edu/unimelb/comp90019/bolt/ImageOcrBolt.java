/*
 * {{ COMP90019 }}
 * Copyright (C) {{ 2019 }}  {{ University of Melbourne }}
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package edu.unimelb.comp90019.bolt;

import java.util.Map;

import org.apache.storm.task.ShellBolt;
import org.apache.storm.topology.IRichBolt;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Fields;

import edu.unimelb.comp90019.TopologyFields;

/**
 * Image OCR Bolt
 *
 * @author Malcolm Chu
 * @version 0.1
 * @since 2019-05-01
 */
public class ImageOcrBolt extends ShellBolt implements IRichBolt {
    public ImageOcrBolt() {
        super("python3", "image_ocr.py");
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields(TopologyFields.IMAGE_OCR_URL,
                TopologyFields.IMAGE_OCR_TEXT,
                TopologyFields.IMAGE_OCR_ORIG_URL,
                TopologyFields.IMAGE_OCR_ROI_URL));
    }

    @Override
    public Map<String, Object> getComponentConfiguration() {
        return null;
    }
}
