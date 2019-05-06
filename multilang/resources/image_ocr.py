# Licensed to the Apache Software Foundation (ASF) under one
# or more contributor license agreements.  See the NOTICE file
# distributed with this work for additional information
# regarding copyright ownership.  The ASF licenses this file
# to you under the Apache License, Version 2.0 (the
# "License"); you may not use this file except in compliance
# with the License.  You may obtain a copy of the License at
#
# http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

# Description: Download tweet images using wget and perform text recognition
# 
# Author: Malcolm Chu

import storm
import os
import wget
import sys
import io
import text_recognition
import numpy as np
import cv2
import image_seaweedfs

class ImageOcrBolt(storm.BasicBolt):
    def process(self, tup):
        local_path_prefix = '/data/tweet/images/'
        orig_suffix = '_orig.jpg'
        roi_suffix = '_roi.jpg'
        
        media_urls = tup.values[8]
        
        # Process if media_urls is not empty
        if media_urls:
            for url in media_urls.split(" "):
                filename_with_ext = os.path.basename(url)
                filename = os.path.splitext(filename_with_ext)[0]
                image_local_path = local_path_prefix + filename_with_ext
                
                # Process image only if we have not seen it before
                if not os.path.isfile(image_local_path):
                    # Storm uses stdout to communicate between multilang bolts
                    # Therefore, need to suppress stdout from wget
                    save_stdout = sys.stdout
                    sys.stdout = io.BytesIO()
                    wget.download(url, image_local_path)
                    sys.stdout = save_stdout
                    
                    # Perform text recognition on the saved image
                    (image_ocr_text, image_ocr_orig_binary, image_ocr_roi_binary) = \
                    text_recognition.image_ocr(image_local_path)
                
                    # Images are encoded in string (jpg format), write them to
                    # the local file system and upload to seaweedfs
                    img_orig_outpath = local_path_prefix + filename + orig_suffix
                    img_roi_outpath = local_path_prefix + filename + roi_suffix
                    
                    image_ocr_orig_url = ''
                    if image_ocr_orig_binary:
                        # Write to local file system
                        nparr = np.fromstring(image_ocr_orig_binary, np.uint8)
                        img_orig = cv2.imdecode(nparr, cv2.IMREAD_COLOR)
                        cv2.imwrite(img_orig_outpath, img_orig)
                        # Upload to seaweedfs
                        image_ocr_orig_url = image_seaweedfs.upload(img_orig_outpath)                        

                    image_ocr_roi_url = ''
                    if image_ocr_roi_binary:
                                                # Write to local file system                    
                        nparr = np.fromstring(image_ocr_roi_binary, np.uint8)
                        img_roi = cv2.imdecode(nparr, cv2.IMREAD_COLOR)
                        cv2.imwrite(img_roi_outpath, img_roi)
                        # Upload to seaweedfs
                        image_ocr_roi_url = image_seaweedfs.upload(img_roi_outpath)

                    # Emit tuple to EsBolt
                    tuple = [url, image_ocr_text, image_ocr_orig_url,
                             image_ocr_roi_url]
                    storm.emit(tuple)

ImageOcrBolt().run()
