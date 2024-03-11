/*
 * Copyright (C) 2020 Seomse Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.seomse.crawling.ha;

import com.seomse.commons.config.Config;
import com.seomse.commons.utils.ExceptionUtil;
import com.seomse.commons.utils.PriorityUtil;
import com.seomse.commons.utils.packages.classes.ClassSearch;
import com.seomse.crawling.CrawlingManager;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * active change
 * @author macle
 */
@Slf4j
public class CrawlingActive {

    /**
     * active 모드가 될때 실행 하는 내용
     */
    static void start(){
        //싱글턴 인스턴스 생성해 놓기
        //noinspection ResultOfMethodCallIgnored
        CrawlingManager.getInstance();

        Comparator<CrawlingActiveInitializer> initializerSort = (i1, i2) -> {
            int seq1 = PriorityUtil.getSeq(i1.getClass());
            int seq2 = PriorityUtil.getSeq(i2.getClass());
            return Integer.compare(seq1, seq2);
        };

        String packagesValue = Config.getConfig(CrawlingHighAvailabilityKey.INITIALIZER_PACKAGE);

        if(packagesValue == null){
            packagesValue = Config.getConfig("default.package", "com.seomse");
        }


        String [] initPackages = packagesValue.split(",");

        ClassSearch search = new ClassSearch();
        search.setInPackages(initPackages);
        Class<?> [] inClasses = {CrawlingActiveInitializer.class};
        search.setInClasses(inClasses);

        List<Class<?>> classes = search.search();

        List<CrawlingActiveInitializer> initializerList = new ArrayList<>();

        for (Class<?> cl : classes) {
            try {
                CrawlingActiveInitializer initializer = (CrawlingActiveInitializer) cl.newInstance();
                initializerList.add(initializer);
            } catch (Exception e) {
                log.error(ExceptionUtil.getStackTrace(e));
            }
        }

        if(initializerList.size() == 0){
            log.debug("crawling active start");
            return;
        }

        CrawlingActiveInitializer[] initializerArray = initializerList.toArray(new CrawlingActiveInitializer[0]);
        initializerList.clear();

        Arrays.sort(initializerArray, PriorityUtil.PRIORITY_SORT);
        //순서 정보가 꼭맞아야하는 정보라 fori 구문 사용 확실한 인지를위해
        //noinspection ForLoopReplaceableByForEach
        for (int i=0 ; i < initializerArray.length ; i++) {
            try{
                initializerArray[i].init();
            }catch(Exception e){log.error(ExceptionUtil.getStackTrace(e));}
        }

        log.debug("crawling active start");
    }

}
