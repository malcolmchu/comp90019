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

# Description: 	Test functions in text_recognition.py and write out the results.
#				Run script locally, not compatible with Apache Storm.
# 
# Author: Malcolm Chu

import text_recognition
import numpy as np
import cv2
import image_seaweedfs

# Example on how to call function image_ocr
(ocr_text, img_orig_str, img_roi_str) = text_recognition.image_ocr('images/example_01.jpg')

# Print ocr text
print(ocr_text)

# Images are encoded in string (jpg format), write them to the local file system
img_orig_outpath = "test_copy.jpg"
img_roi_outpath = "test_roi.jpg"

if img_orig_str:
    nparr = np.fromstring(img_orig_str, np.uint8)
    img_orig = cv2.imdecode(nparr, cv2.IMREAD_COLOR)
    cv2.imwrite(img_orig_outpath, img_orig)

if img_roi_str:
    nparr = np.fromstring(img_roi_str, np.uint8)
    img_roi = cv2.imdecode(nparr, cv2.IMREAD_COLOR)
    cv2.imwrite(img_roi_outpath, img_roi)

# Upload image to a seaweedfs server
result = image_seaweedfs.upload('images/example_01.jpg')
print(result)