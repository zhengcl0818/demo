package com.algorithm.demo.service;

import com.algorithm.demo.algorithm.SFUI_UF.SFUI_UF;
import com.algorithm.demo.algorithm.SFU_Miner.AlgoSFUPMinerUemax;
import com.algorithm.demo.entity.AlgoResult;
import com.algorithm.demo.enumeration.StatusEnum;
import com.algorithm.demo.resp.Resp;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.util.*;

import static com.algorithm.demo.configuration.Constant.Algo_SFUI_UF;
import static com.algorithm.demo.configuration.Constant.Algo_SFU_Miner;

/**
 * @描述:
 * @author: zhengchuanlong
 * @date: 2021/12/30 下午12:27
 */
@Service("runAlgorithm")
@Log4j2
public class RunAlgorithm {
    public Resp<Object> runAlgo(Map<String, String[]> dataMap){
        //store the algorithm results
        //初始化,PSFUI,SearchCount,Time,Memory
        Map<String,List<Object>> resultMap = new HashMap<>();
        Map<String,List<Object>> runtimeMap=new HashMap<>();
        Map<String,List<Object>> memoryMap=new HashMap<>();
        Map<String,List<Object>> PSFUIMap=new HashMap<>();
        Map<String,List<Object>> searchSpaceMap=new HashMap<>();
        int size=0;
        List<String> algorithm = new ArrayList<>();
        //input file
        try {
            for (String data : dataMap.get("data")) {
                File file = ResourceUtils.getFile("classpath:dataset/" + data);
                List<Object>runtimeList=new ArrayList<>();
                runtimeList.add(data);
                List<Object>memoryList=new ArrayList<>();
                memoryList.add(data);
                List<Object>PSFUIList=new ArrayList<>();
                PSFUIList.add(data);
                List<Object>searchSpaceList=new ArrayList<>();
                searchSpaceList.add(data);
                //需要运行的算法用algo数组存储
                algorithm = new ArrayList<>(Arrays.asList(ArrayUtils.addAll(dataMap.get("source"), dataMap.get("target"))));
                //循环执行全部算法，todo 可以抽取成一个配置
                for (String algoName : algorithm) {

                    if (algoName.equals(Algo_SFUI_UF)) {
                        AlgoResult algoSFUI_UFResult = new AlgoResult();
                        try {
                            //SFUI_UF
                            SFUI_UF uf = new SFUI_UF();
                            uf.runSFUI_UF(file);
                            algoSFUI_UFResult = uf.getResult();
                            algoSFUI_UFResult.setDataName(data);
                            runtimeList.add(Double.parseDouble(algoSFUI_UFResult.getRunTime()));
                            memoryList.add(Double.parseDouble(algoSFUI_UFResult.getRunMemory()));
                            PSFUIList.add(Double.parseDouble(algoSFUI_UFResult.getPSFUI()));
                            searchSpaceList.add(Double.parseDouble(algoSFUI_UFResult.getSearchSpace()));
                            //algoResultList.add(algoSFUI_UFResult);
                            log.info("Algorithm SFUI-UF run finished");
                        } catch (Exception e) {
                            log.info("SFUI-UF Exception {}", String.valueOf(e));
                            //algoResultList.add(algoSFUI_UFResult);
                        }
                    } else if (algoName.equals(Algo_SFU_Miner)) {
                        AlgoResult algoSFU_MinerResult = new AlgoResult();
                        try {
                            //SFU_Miner
                            AlgoSFUPMinerUemax sfupMine = new AlgoSFUPMinerUemax();
                            sfupMine.runAlgorithm(file);
                            algoSFU_MinerResult = sfupMine.getAlgoResult();
                            algoSFU_MinerResult.setDataName(data);
                            runtimeList.add(Double.parseDouble(algoSFU_MinerResult.getRunTime()));
                            memoryList.add(Double.parseDouble(algoSFU_MinerResult.getRunMemory()));
                            PSFUIList.add(Double.parseDouble(algoSFU_MinerResult.getPSFUI()));
                            searchSpaceList.add(Double.parseDouble(algoSFU_MinerResult.getSearchSpace()));
                            //algoResultList.add(algoSFU_MinerResult);
                            log.info("Algorithm SFU-Miner run finished");
                        } catch (Exception e) {
                            log.info("SFU-Miner Exception {}", String.valueOf(e));
                            //algoResultList.add(algoSFU_MinerResult);
                        }
                    }
                    runtimeMap.put(data,runtimeList);
                    memoryMap.put(data,memoryList);
                    PSFUIMap.put(data,PSFUIList);
                    searchSpaceMap.put(data,searchSpaceList);
                }
            }

            List<Object> resultRuntimeList = new ArrayList<>(runtimeMap.values());
            List<Object> resultMemoryList = new ArrayList<>(memoryMap.values());
            List<Object> resultPSFUIList = new ArrayList<>(PSFUIMap.values());
            List<Object> resultSearchSpaceList = new ArrayList<>(searchSpaceMap.values());

            resultMap.put("runtime",resultRuntimeList);
            resultMap.put("memory",resultMemoryList);
            resultMap.put("PSFUI",resultPSFUIList);
            resultMap.put("searchSpace",resultSearchSpaceList);
            return new Resp<>(StatusEnum.ALGO_RUN_SUCCESS.getStatusCode(),
                    StatusEnum.ALGO_RUN_SUCCESS.getStatusMsg(),
                    resultMap);
        } catch (Exception e) {
            return new Resp<>(StatusEnum.ALGO_RUN_FAIL.getStatusCode(),
                    StatusEnum.ALGO_RUN_FAIL.getStatusMsg(),
                    e);
        }
    }
}
