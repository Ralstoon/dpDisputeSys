package com.seu.utils;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class GetTitleAndAbstractTest {

    @Test
    public void generateCaseTitleDetail() {
        String s=" [{\n" +
                "\t\t\"ResultOfDamage\": \"损害结果\",\n" +
                "\t\t“ResultList”: {\n" +
                "\t\t\t\"medcine\": {\n" +
                "\t\t\t\t\"behavior\": [\"失误1\", \"失误2\"],\n" +
                "\t\t\t\t\"medicine\": \"药名\"\n" +
                "\t\t\t},\n" +
                "\t\t\t\"operator\": {\n" +
                "\t\t\t\t\"behavior\": [\"失误1\", \"失误2\"],\n" +
                "\t\t\t\t\"syndrome\": \"症状\",\n" +
                "\t\t\t\t\"operation\": \"手术名称\"\n" +
                "\t\t\t},\n" +
                "\t\t\t\"diagnosis\": {\n" +
                "\t\t\t\t\"behavior\": [\"失误1\", \"失误2\"]\n" +
                "\t\t\t},\n" +
                "\t\t\t\"treatment\": {\n" +
                "\t\t\t\t\"behavior\": [\"失误1\", \"失误2\"]\n" +
                "\t\t\t},\n" +
                "\t\t\t\"anesthesia\": {\n" +
                "\t\t\t\t\"behavior\": [\"失误1\", \"失误2\"]\n" +
                "\t\t\t},\n" +
                "\t\t\t\"management\": {\n" +
                "\t\t\t\t\"behavior\": [\"失误1\", \"失误2\"]\n" +
                "\t\t\t},\n" +
                "\t\t\t\"transfusion\": {\n" +
                "\t\t\t\t\"behavior\": [\"失误1\", \"失误2\"]\n" +
                "\t\t\t},\n" +
                "\t\t\t\"verification\": {\n" +
                "\t\t\t\t\"testt\": \"检验内容\",\n" +
                "\t\t\t\t\"behavior\": [\"失误1\", \"失误2\"]\n" +
                "\t\t\t}\n" +
                "\t\t},\n" +
                "\t\t\"DiseaseListAfter\": [{\n" +
                "\t\t\t\"DiseaseKind\": \"类型\",\n" +
                "\t\t\t\"DiseaseName\": \"名称\"\n" +
                "\t\t}],\n" +
                "\t\t\"DiseaseListBefore\": [{\n" +
                "\t\t\t\"DiseaseKind\": \"类型\",\n" +
                "\t\t\t\"DiseaseName\": \"名称\"\n" +
                "\t\t}],\n" +
                "\t\t\"InvolvedInstitute\": [{\n" +
                "\t\t\t\"City\": \"南京市\",\n" +
                "\t\t\t\"Zone\": \"栖霞区\",\n" +
                "\t\t\t\"Hospital\": \"仙林医院\",\n" +
                "\t\t\t\"Department\": \"骨科\"\n" +
                "\t\t}, {\n" +
                "\t\t\t\"City\": \"南京市\",\n" +
                "\t\t\t\"Zone\": \"鼓楼区\",\n" +
                "\t\t\t\"Hospital\": \"鼓楼医院\",\n" +
                "\t\t\t\"Department\": \"骨科\"\n" +
                "\t\t}],\n" +
                "\t\t\"DiseasesymptomAfter\": \"症状2\",\n" +
                "\t\t\"DiseasesymptomBefore\": \"症状1\"\n" +
                "\t}]";
        List<String> names=new ArrayList<>();
        names.add("吴宇航");
        Map<String,String> map=GetTitleAndAbstract.generateCaseTitleDetail(s,names);

    }
}