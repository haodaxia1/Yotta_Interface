package com.xjtu.dependency.controller;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.google.gson.JsonArray;
import com.xjtu.assemble.domain.Assemble;
import com.xjtu.assemble.domain.AssembleContainType;
import com.xjtu.assemble.repository.AssembleRepository;
import com.xjtu.common.domain.Result;
import com.xjtu.common.domain.ResultEnum;
import com.xjtu.dependency.domain.*;
import com.xjtu.dependency.service.DependencyService;
import com.xjtu.facet.domain.Facet;
import com.xjtu.facet.domain.FacetContainAssemble;
import com.xjtu.facet.repository.FacetRepository;
import com.xjtu.topic.controller.TopicController;
import com.xjtu.topic.domain.Topic;
import com.xjtu.topic.domain.TopicContainFacet;
import com.xjtu.topic.repository.TopicRepository;
import com.xjtu.utils.HttpUtil;
import com.xjtu.utils.ResultUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.*;

/**
 * api:处理主题依赖关系
 *
 * @author:yangkuan
 * @date:2018/03/21 13:19
 */
@RestController
@RequestMapping(value = "dependency")
public class DependencyController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    DependencyService dependencyService;

    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private FacetRepository facetRepository;

    @Autowired
    private AssembleRepository assembleRepository;

    @PostMapping("/insertDependency")
    @ApiOperation(value = "通过主课程名，在课程下的插入、添加主题依赖关系", notes = "通过主课程名，在课程下的插入、添加主题依赖关系")
    public ResponseEntity insertDependency(@RequestParam(name = "domainName") String domainName
            , @RequestParam(name = "startTopicName") String startTopicName
            , @RequestParam(name = "endTopicName") String endTopicName) {

        Result result = dependencyService.insertDependency(domainName, startTopicName, endTopicName);
        if (!result.getCode().equals(ResultEnum.SUCCESS.getCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @PostMapping("/deleteDependency")
    @ApiOperation(value = "通过主课程名，起始、终止主题id删除依赖关系", notes = "通过主课程名，起始、终止主题id删除依赖关系")
    public ResponseEntity deleteDependency(@RequestParam(name = "domainName") String domainName
            , @RequestParam(name = "startTopicId") Long startTopicId
            , @RequestParam(name = "endTopicId") Long endTopicId) {
        Result result = dependencyService.deleteDependency(domainName, startTopicId, endTopicId);
        if (!result.getCode().equals(ResultEnum.SUCCESS.getCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @PostMapping("/deleteDependencyByTopicName")
    @ApiOperation(value = "通过主课程名，起始、终止主题名删除依赖关系", notes = "通过主课程名，起始、终止主题名删除依赖关系")
    public ResponseEntity deleteDependencyByTopicName(@RequestParam(name = "domainName") String domainName
            , @RequestParam(name = "startTopicName") String startTopicName
            , @RequestParam(name = "endTopicName") String endTopicName) {
        Result result = dependencyService.deleteDependencyByTopicName(domainName, startTopicName, endTopicName);
        if (!result.getCode().equals(ResultEnum.SUCCESS.getCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    /**
     * API
     * 通过课程名和关键词，获取该课程下的主题依赖关系
     */
    @GetMapping("/getDependenciesByKeyword")
    @ApiOperation(value = "通过关键词，获取该课程下的主题依赖关系", notes = "通过关键词，获取该课程下的主题依赖关系")
    public ResponseEntity getDependenciesByKeyword(@RequestParam(name = "domainName") String domainName
            , @RequestParam(name = "keyword") String keyword) {
        Result result = dependencyService.findDependenciesByKeyword(domainName, keyword);
        if (!result.getCode().equals(ResultEnum.SUCCESS.getCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    /**
     * Author:haozichen
     * 调用python接口为前端可视化做数据转换
     */
    @GetMapping("/getDependences")
    @ApiOperation(value = "调用python接口为前端可视化做数据转换", notes = "调用python接口为前端可视化做数据转换")
    public String getDependences(@RequestParam(name = "domainName") String domainName) throws Exception {
        String url = "http://47.95.145.72/dependences/?domainName=" + domainName;
        ItemStyle.init();
        RestTemplate restTemplate = new RestTemplate();
        Map<String, String> params = new HashMap<>();
        params.put("name", "dada");  //
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(url, String.class, params);
        String res = responseEntity.getBody();
        JSONObject jsonObject = JSON.parseObject(res);
        String topicsJsonObject = jsonObject.getString("topics").toString().replaceAll("\\{", "").replaceAll("\\}", "");
        Map<String, String> map = new HashMap<>();
        for (String s : topicsJsonObject.split(",")) {
            String[] element = s.split(":");
            if (element.length == 2) {
                map.put(element[0].substring(1, element[0].length() - 1), element[1].substring(1, element[1].length() - 1));
            }
        }
        String graphJsonObject = jsonObject.getString("graph");
        String graphStr = graphJsonObject.substring(1, graphJsonObject.length() - 1);
        System.out.println(graphStr);
        String[] ss = graphStr.split("\\},");
        List<DependencyData> list = new ArrayList<>();
        int count = 0;
        for (int i = 0; i < ss.length; i++) {
            String s = ss[i];
            String[] element = s.split(":\\{");
            long parentId = Long.MAX_VALUE;
            Set<String> childIds = new HashSet<>();
            if (element.length == 2) {
                String[] s1 = element[1].split("\\],");
                for (String e : s1) {
                    e = e.replaceAll("\\[", "").replaceAll("\\]", "");
                    String[] resEle = e.split(":");
                    if (resEle.length > 1) {
                        long parTempId = Long.valueOf(resEle[0].substring(1, resEle[0].length() - 1));
                        parentId = Math.min(parentId, parTempId);
                        childIds.add(String.valueOf(parentId));
                        String[] resChild = resEle[1].split(",");
                        for (String sss : resChild) {
                            childIds.add(sss);
                        }
                    }
                }
            }
            if (parentId != Long.MAX_VALUE) {
                childIds.remove(String.valueOf(parentId));
                DependencyData par = new DependencyData();
                ItemStyle itemStyle = new ItemStyle(count, 0);
                itemStyle.setGroupId(i);
                par.setItemStyle(itemStyle);
                int c2 = 1;
                par.setName(map.get(String.valueOf(parentId)));
                List<DependencyData> child = new ArrayList<>();
                for (String id : childIds) {
                    String topicName = map.get(id);
                    TopicContainFacet facetContain = getFacetByTopicName(topicName, "yes");
                    if (facetContain != null) {
                        List<Facet> facets = facetContain.getChildren();
                        DependencyData data = new DependencyData(topicName, new ItemStyle(count, c2++));
                        List<DependencyData> children = new ArrayList<>();
                        if (facets.isEmpty() || facets.size() > 0) {
                            for (Facet facet : facets) {
                                if (facet != null && facet.getFacetId() != null)
                                    children.add(new DependencyData(facet.getFacetName(), new ItemStyle(count, c2++, facet.getFacetId()), 1));
                                else
                                    children.add(new DependencyData("", new ItemStyle(count, c2++), 1));
                            }
                            data.setChildren(children);
                            child.add(data);
                        } else {
                            child.add(new DependencyData(topicName, new ItemStyle(count, c2++), 1));
                        }
                    }
                }
                par.setChildren(child);
                list.add(par);
                count++;
            }
        }
        String ans = JSON.toJSONString(list, SerializerFeature.WriteDateUseDateFormat);
        return ans;
    }

    /**
     * Author:haozichen
     * 调用python接口为前端可视化做数据转换
     */
    @GetMapping("/getGroupPath")
    @ApiOperation(value = "快速调用python接口为前端知识簇可视化做数据转换", notes = "快速调用python接口为前端知识簇可视化做数据转换")
    public String getGroupPath(@RequestParam(name = "domainName") String domainName) throws Exception {
        String url = "http://47.95.145.72/dependences/?domainName=" + domainName;
        RestTemplate restTemplate = new RestTemplate();
        Map<String, String> params = new HashMap<>();
        params.put("name", "dada");  //
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(url, String.class, params);
        String res = responseEntity.getBody();
        JSONObject jsonObject = JSON.parseObject(res);
        String communityRelation = jsonObject.getString("communityRelation").replaceAll("\\{", "").replaceAll("\\}", "");
        Map<String, String> map = groupIdAndNameRealtion(jsonObject);
        List<GraphData> graphDataList = new ArrayList<>();
        int count = 0;
        for (int i = 0; i < map.size(); i++) {
            graphDataList.add(new GraphData(map.get(i + ""), 70, "", count++));
        }
        List<GraphLink> graphLinkList = new ArrayList<>();
        for (String s : communityRelation.split("\\],")) {
            String[] link = s.split(":\\[");
            if(link.length>1){
                String resourceName = "";
                if (link[0].length() >= 2) resourceName = map.get(link[0].substring(1, link[0].length() - 1));
                link[1] = link[1].replaceAll("\\]", "");
                for (String ss : link[1].split(",")) {
                    graphLinkList.add(new GraphLink(resourceName, map.get(ss), "", ""));
                }
            }
        }
        String ans = JSON.toJSONString(new DependencyGraph(graphDataList, graphLinkList));
        return ans;
    }

    public Map<String, String> groupIdAndNameRealtion(JSONObject jsonObject) {
        JSONObject topics = JSON.parseObject(jsonObject.getString("topics"));
        JSONObject graph = JSON.parseObject(jsonObject.getString("graph"));
        Map<String, String> map = new HashMap<>();
        for (int i = 0; i < 50; i++) {
            if (graph.getString(i + "") == null || graph.getString(i + "").isEmpty()) break;
            String json = graph.getString(i + "").replaceAll("\\{", "").replaceAll("\\}", "")
                    .replaceAll("\\[", "").replaceAll("\\]", "");

            String[] par = json.split(",\"");
            int min = 1000000;
            for (String s : par) {
                String ss = s.split(":")[0];
                if (min == 1000000) min = Math.min(Integer.valueOf(ss.substring(1, ss.length() - 1)), min);
                else min = Math.min(Integer.valueOf(ss.substring(0, ss.length() - 1)), min);
            }
            if (min != 10000)
                map.put(i + "", topics.getString(min + ""));
        }
        return map;
    }

    /**
     * Author:haozichen
     * 调用python接口为前端学习路径可视化做数据转换
     */
    @GetMapping("/getGroupDependences")
    @ApiOperation(value = "调用python接口为前端可视化做数据转换", notes = "调用python接口为前端可视化做数据转换")
    public String getGroupDependences(@RequestParam(name = "domainName") String domainName, long groupId) throws Exception {
        String url = "http://47.95.145.72/dependences/?domainName=" + domainName;
        RestTemplate restTemplate = new RestTemplate();
        Map<String, String> params = new HashMap<>();
        params.put("name", "dada");  //
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(url, String.class, params);
        String res = responseEntity.getBody();
        JSONObject jsonObject = JSON.parseObject(res);
        JSONObject jsonObjectofGroup = JSON.parseObject(jsonObject.getString("graph"));
        JSONObject topics = JSON.parseObject(jsonObject.getString("topics"));
        String graphGroupId = jsonObjectofGroup.getString(groupId + "").replaceAll("\\{", "").replaceAll("\\}", "");
        List<GraphData> graphDataList = new ArrayList<>();
        Set<String> dataSet = new HashSet<>();
        List<GraphLink> graphLinkList = new ArrayList<>();
        int count = 0;
        int descount = 0;
        for (String s : graphGroupId.split("\\],")) {
            String[] ss = s.replaceAll("\\]", "").split(":\\[");
            if (ss.length > 1) {
                String resourceId = ss[0];
                resourceId = resourceId.substring(1, resourceId.length() - 1);
                Object r = topics.get(resourceId);
                String resourceName = topics.get(resourceId).toString();
                GraphData resource = new GraphData(resourceName, 70, "linknode" + descount++, count++);
                if (!dataSet.contains(resourceId)) {
                    dataSet.add(resourceId);
                    graphDataList.add(resource);
                }
                int nameId = 0;
                for (String childId : ss[1].split(",")) {
                    String targetName = topics.get(childId).toString();
                    if (!dataSet.contains(childId)) {
                        dataSet.add(childId);
                        GraphData child = new GraphData(targetName, 70, "linknode" + descount++, count++);
                        graphDataList.add(child);
                    }
                    GraphLink graphLink = new GraphLink(resourceName, targetName, "", resource.getDes());
                    graphLinkList.add(graphLink);
                }
            }
        }
        String ans = JSON.toJSONString(new DependencyGraph(graphDataList, graphLinkList));
        return ans;
    }

    public TopicContainFacet getFacetByTopicName(String topicName, String hasFragment) {
        List<Topic> topicList = topicRepository.findByTopicName(topicName);

        if (topicList.size() <= 0) return new TopicContainFacet("", "", null, null, new ArrayList<>());
        Topic topic = topicList.get(0);
        List<Facet> firstLayerFacets = facetRepository.findByTopicIdAndFacetLayer(topic.getTopicId(), 1);
        List<Facet> secondLayerFacets = facetRepository.findByTopicIdAndFacetLayer(topic.getTopicId(), 2);
        List<Assemble> assembles = assembleRepository.findAllAssemblesByTopicId(topic.getTopicId());
        //初始化Topic
        TopicContainFacet topicContainFacet = new TopicContainFacet();
        topicContainFacet.setTopic(topic);
        topicContainFacet.setChildrenNumber(firstLayerFacets.size());

        //firstLayerFacets一级分面列表，将二级分面挂到对应一级分面下
        List<Facet> firstLayerFacetContainAssembles = new ArrayList<>();
        for (Facet firstLayerFacet : firstLayerFacets) {
            if (firstLayerFacet.getFacetName().equals("匿名分面")) {
                continue;
            }
            FacetContainAssemble firstLayerFacetContainAssemble = new FacetContainAssemble();
            firstLayerFacetContainAssemble.setFacet(firstLayerFacet);
            firstLayerFacetContainAssemble.setType("branch");
            //设置一级分面的子节点（二级分面）
            List<Object> secondLayerFacetContainAssembles = new ArrayList<>();
            for (Facet secondLayerFacet : secondLayerFacets) {
                //一级分面下的二级分面
                if (secondLayerFacet.getParentFacetId().equals(firstLayerFacet.getFacetId())) {
                    FacetContainAssemble secondLayerFacetContainAssemble = new FacetContainAssemble();
                    secondLayerFacetContainAssemble.setFacet(secondLayerFacet);
                    List<Object> assembleContainTypes = new ArrayList<>();
                    for (Assemble assemble : assembles) {
                        //二级分面下的碎片
                        if (assemble.getFacetId().equals(secondLayerFacet.getFacetId())) {
                            AssembleContainType assembleContainType = new AssembleContainType();
                            if ("emptyAssembleContent".equals(hasFragment)) {
                                assemble.setAssembleContent("");
                            }
                            assembleContainType.setAssemble(assemble);
                            String ip = HttpUtil.getIp();
                            assembleContainType.setUrl(ip + ":" + 8081 + "/assemble/getAssembleContentById?assembleId=" + assemble.getAssembleId());
                            assembleContainTypes.add(assembleContainType);
                        }
                    }
                    secondLayerFacetContainAssemble.setChildren(assembleContainTypes);
                    secondLayerFacetContainAssemble.setChildrenNumber(assembleContainTypes.size());
                    secondLayerFacetContainAssembles.add(secondLayerFacetContainAssemble);
                }
            }
            //一级分面有二级分面
            if (secondLayerFacetContainAssembles.size() > 0) {
                firstLayerFacetContainAssemble.setChildren(secondLayerFacetContainAssembles);
                firstLayerFacetContainAssemble.setChildrenNumber(secondLayerFacetContainAssembles.size());
                firstLayerFacetContainAssemble.setContainChildrenFacet(true);
            }
            //一级分面没有二级分面
            else {
                firstLayerFacetContainAssemble.setContainChildrenFacet(false);
                List<Object> assembleContainTypes = new ArrayList<>();
                for (Assemble assemble : assembles) {
                    //一级分面下的碎片
                    if (assemble.getFacetId().equals(firstLayerFacet.getFacetId())) {
                        AssembleContainType assembleContainType = new AssembleContainType();
                        if ("emptyAssembleContent".equals(hasFragment)) {
                            assemble.setAssembleContent("");
                        }
                        assembleContainType.setAssemble(assemble);
                        String ip = HttpUtil.getIp();
                        assembleContainType.setUrl(ip + ":" + 8081 + "/assemble/getAssembleContentById?assembleId=" + assemble.getAssembleId());

                        assembleContainTypes.add(assembleContainType);
                    }
                }
                firstLayerFacetContainAssemble.setChildren(assembleContainTypes);
                firstLayerFacetContainAssemble.setChildrenNumber(assembleContainTypes.size());
            }
            firstLayerFacetContainAssembles.add(firstLayerFacetContainAssemble);
        }
        topicContainFacet.setChildren(firstLayerFacetContainAssembles);
        topicContainFacet.setChildrenNumber(firstLayerFacetContainAssembles.size());
        return topicContainFacet;
    }

    /**
     * API
     * 通过课程名，获取该课程下的主题依赖关系
     */
    @GetMapping("/getDependenciesByDomainName")
    @ApiOperation(value = "通过课程名，获取该课程下的主题依赖关系", notes = "通过课程名，获取该课程下的主题依赖关系")
    public ResponseEntity getDependenciesByDomainName(@RequestParam(name = "domainName") String domainName) {
        Result result = dependencyService.findDependenciesByDomainName(domainName);
        if (!result.getCode().equals(ResultEnum.SUCCESS.getCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    /**
     * API
     * 通过主课程名，获取该课程下的主题依赖关系，运行生成社团关系，并保存为gexf文件
     */
    @PostMapping("/getDependenciesByDomainNameSaveAsGexf")
    @ApiOperation(value = "通过主课程名，获取该课程下的主题依赖关系，运行生成社团关系，并保存为gexf文件"
            , notes = "通过主课程名，获取该课程下的主题依赖关系，运行生成社团关系，并保存为gexf文件")
    public ResponseEntity getDependenciesByDomainNameSaveAsGexf(@RequestParam(name = "domainName") String domainName) {
        Result result = dependencyService.findDependenciesByDomainNameSaveAsGexf(domainName);
        if (!result.getCode().equals(ResultEnum.SUCCESS.getCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    /**
     * API
     * 自动构建主题依赖关系
     * 通过课程名，生成该课程下主题依赖关系。需已有主题，碎片信息
     */
    @PostMapping("/generateDependencyByDomainName")
    @ApiOperation(value = "自动构建主题依赖关系。通过课程名，生成该课程下主题依赖关系。需已有主题，碎片信息。并给出该课程是否为英文课程信息",
            notes = "自动构建主题依赖关系。通过课程名，生成该课程下主题依赖关系。需已有主题，碎片信息。并给出该课程是否为英文课程信息")
    public ResponseEntity generateDependencyByDomainName(@RequestParam(name = "domainName") String domainName
            , @RequestParam(name = "isEnglish") boolean isEnglish) {
        Result result = dependencyService.generateDependencyByDomainName(domainName, isEnglish);
        if (!result.getCode().equals(ResultEnum.SUCCESS.getCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }
}
