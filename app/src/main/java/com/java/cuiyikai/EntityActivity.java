package com.java.cuiyikai;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.java.cuiyikai.adapters.PropertyAdapter;
import com.java.cuiyikai.adapters.RelationAdapter;
import com.java.cuiyikai.entities.PropertyEntity;
import com.java.cuiyikai.entities.RelationEntity;
import com.java.cuiyikai.widgets.ListViewForScrollView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;


public class EntityActivity extends AppCompatActivity {

    private class RelationViewOnClickListener implements View.OnClickListener {
        private final List<RelationEntity> fullList, prevList;
        private boolean extended;
        private final ListViewForScrollView relatedView;
        private final ImageButton relatedButton;

        @Override
        public void onClick(View v) {
            if(extended) {
                relatedButton.setBackgroundResource(R.drawable.pulldown);
                extended = false;
                relatedView.setAdapter(new RelationAdapter(EntityActivity.this, R.layout.relation_item, prevList));
            } else {
                relatedButton.setBackgroundResource(R.drawable.pullback);
                extended = true;
                relatedView.setAdapter(new RelationAdapter(EntityActivity.this, R.layout.relation_item, fullList));
            }
        }

        public RelationViewOnClickListener(List<RelationEntity> fullList, List<RelationEntity> prevList, boolean extended, ListViewForScrollView relatedView, ImageButton relatedButton) {
            this.fullList = fullList;
            this.prevList = prevList;
            this.extended = extended;
            this.relatedView = relatedView;
            this.relatedButton = relatedButton;
        }
    }

    private class PropertyViewOnClickListener implements View.OnClickListener {
        private final List<PropertyEntity> fullList, prevList;
        private boolean extended;
        private final ListViewForScrollView propertyView;
        private final ImageButton propertyButton;

        @Override
        public void onClick(View v) {
            if(extended) {
                propertyButton.setBackgroundResource(R.drawable.pulldown);
                extended = false;
                propertyView.setAdapter(new PropertyAdapter(EntityActivity.this, R.layout.property_item, prevList));
            } else {
                propertyButton.setBackgroundResource(R.drawable.pullback);
                extended = true;
                propertyView.setAdapter(new PropertyAdapter(EntityActivity.this, R.layout.property_item, fullList));
            }
        }

        public PropertyViewOnClickListener(List<PropertyEntity> fullList, List<PropertyEntity> prevList, boolean extended, ListViewForScrollView propertyView, ImageButton propertyButton) {
            this.fullList = fullList;
            this.prevList = prevList;
            this.extended = extended;
            this.propertyView = propertyView;
            this.propertyButton = propertyButton;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entity);
        ImageButton relationButton = (ImageButton) findViewById(R.id.relationButton);
        ImageButton propertyButton = (ImageButton) findViewById(R.id.propertyButton);
        String entityName = "李白";
        JSONObject entityJson = JSON.parseObject("{\n" +
                "        \"property\": [\n" +
                "            {\n" +
                "                \"predicate\": \"http://www.w3.org/1999/02/22-rdf-syntax-ns#type\",\n" +
                "                \"predicateLabel\": \"类型\",\n" +
                "                \"object\": \"http://www.w3.org/2002/07/owl#NamedIndividual\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"predicate\": \"http://edukg.org/knowledge/0.1/property/chinese#shidai-10c4265e611dce9383da6b0c084621a2\",\n" +
                "                \"predicateLabel\": \"时代\",\n" +
                "                \"object\": \"盛唐\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"predicate\": \"http://edukg.org/knowledge/0.1/property/common#definition\",\n" +
                "                \"predicateLabel\": \"定义\",\n" +
                "                \"object\": \"字太白，号青莲居士，有“诗仙”之称，唐代浪漫主义诗人\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"predicate\": \"http://edukg.org/knowledge/0.1/property/chinese#siwangriqi-53f3bf969fd37d3e1f5d5a32048274d5\",\n" +
                "                \"predicateLabel\": \"死亡日期\",\n" +
                "                \"object\": \"762\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"predicate\": \"http://edukg.org/knowledge/0.1/property/chinese#zhiye-9d3dc0e7d9e40bb94f20e8c623a12aed\",\n" +
                "                \"predicateLabel\": \"职业\",\n" +
                "                \"object\": \"有“诗仙”之称，唐代浪漫主义诗人\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"predicate\": \"http://edukg.org/knowledge/0.1/property/common#source\",\n" +
                "                \"predicateLabel\": \"出处\",\n" +
                "                \"object\": \"http://kb.cs.tsinghua.edu.cn/apibztask/label/1955#xpointer(start-point(string-range(//BODY/P[40]/text()[1],'',0))/range-to(string-range(//BODY/P[40]/text()[1],'',2)))\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"predicate\": \"http://edukg.org/knowledge/0.1/property/common#source\",\n" +
                "                \"predicateLabel\": \"出处\",\n" +
                "                \"object\": \"http://kb.cs.tsinghua.edu.cn/apibztask/label/1943#xpointer(start-point(string-range(//BODY/P[25]/text()[1],'',13))/range-to(string-range(//BODY/P[25]/text()[1],'',15)))\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"predicate\": \"http://edukg.org/knowledge/0.1/property/common_candidate#meaning\",\n" +
                "                \"predicateLabel\": \"涵义\",\n" +
                "                \"object\": \"字太白，号青莲居士，有“诗仙”之称，唐代浪漫主义诗人\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"predicate\": \"http://edukg.org/knowledge/0.1/property/common#source\",\n" +
                "                \"predicateLabel\": \"出处\",\n" +
                "                \"object\": \"http://kb.cs.tsinghua.edu.cn/apibztask/label/3663#xpointer(start-point(string-range(//BODY/P[28]/SPAN[1]/text()[1],'',3))/range-to(string-range(//BODY/P[28]/SPAN[1]/text()[1],'',5)))\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"predicate\": \"http://edukg.org/knowledge/0.1/property/common_candidate#meaning\",\n" +
                "                \"predicateLabel\": \"含义\",\n" +
                "                \"object\": \"字太白，号青莲居士，有“诗仙”之称，唐代浪漫主义诗人\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"predicate\": \"http://edukg.org/knowledge/0.1/property/chinese#chushengriqi-3252a008e81e89518ef649930e84bf65\",\n" +
                "                \"predicateLabel\": \"出生日期\",\n" +
                "                \"object\": \"701\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"predicate\": \"http://edukg.org/knowledge/0.1/property/common#source\",\n" +
                "                \"predicateLabel\": \"出处\",\n" +
                "                \"object\": \"http://kb.cs.tsinghua.edu.cn/apibztask/label/706#xpointer(start-point(string-range(//BODY/DIV[1]/P[1]/text()[1],'',0))/range-to(string-range(//BODY/DIV[1]/P[1]/text()[1],'',2)))\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"predicate\": \"http://edukg.org/knowledge/0.1/property/chinese#zi-144af082fbe363a4911e5e2f9838076c\",\n" +
                "                \"predicateLabel\": \"字\",\n" +
                "                \"object\": \"太白\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"predicate\": \"http://www.w3.org/1999/02/22-rdf-syntax-ns#type\",\n" +
                "                \"predicateLabel\": \"类型\",\n" +
                "                \"object\": \"http://edukg.org/knowledge/0.1/class/chinese#zuojia-8bc144ece4c0839df3d332ab13f8b885\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"predicate\": \"http://edukg.org/knowledge/0.1/property/common#content\",\n" +
                "                \"predicateLabel\": \"内容\",\n" +
                "                \"object\": \"（701—762）　唐代最伟大的浪漫主义诗人。字太白，号青莲居士，后人称其为“诗仙”，陇西成纪人。代表作有《渡荆门送别》《望天门山》《闻王昌龄左迁龙标遥有此寄》《月下独酌》《宣州谢朓楼饯别校书叔云》《行路难》等。\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"predicate\": \"http://edukg.org/knowledge/0.1/property/common#source\",\n" +
                "                \"predicateLabel\": \"出处\",\n" +
                "                \"object\": \"http://kb.cs.tsinghua.edu.cn/apibztask/label/1937#xpointer(start-point(string-range(//BODY/P[17]/text()[1],'',2))/range-to(string-range(//BODY/P[17]/text()[1],'',4)))\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"predicate\": \"http://edukg.org/knowledge/0.1/property/common#source\",\n" +
                "                \"predicateLabel\": \"出处\",\n" +
                "                \"object\": \"http://kb.cs.tsinghua.edu.cn/apibztask/label/1937#xpointer(start-point(string-range(//BODY/P[16]/text()[1],'',4))/range-to(string-range(//BODY/P[16]/text()[1],'',6)))\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"predicate\": \"http://edukg.org/knowledge/0.1/property/common#source\",\n" +
                "                \"predicateLabel\": \"出处\",\n" +
                "                \"object\": \"http://kb.cs.tsinghua.edu.cn/apibztask/label/698#xpointer(start-point(string-range(//BODY/DIV[1]/P[3]/text()[1],'',0))/range-to(string-range(//BODY/DIV[1]/P[3]/text()[1],'',2)))\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"predicate\": \"http://edukg.org/knowledge/0.1/property/chinese#hao-8a93ea3b3c6f75da6b0f08b06bb3d19c\",\n" +
                "                \"predicateLabel\": \"号\",\n" +
                "                \"object\": \"青莲居士\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"predicate\": \"http://edukg.org/knowledge/0.1/property/common#source\",\n" +
                "                \"predicateLabel\": \"出处\",\n" +
                "                \"object\": \"http://kb.cs.tsinghua.edu.cn/apibztask/label/1931#xpointer(start-point(string-range(//BODY/TABLE[2]/TBODY[1]/TR[7]/TD[1]/text()[1],'',0))/range-to(string-range(//BODY/TABLE[2]/TBODY[1]/TR[7]/TD[1]/text()[1],'',2)))\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"predicate\": \"http://edukg.org/knowledge/0.1/property/common#source\",\n" +
                "                \"predicateLabel\": \"出处\",\n" +
                "                \"object\": \"http://kb.cs.tsinghua.edu.cn/apibztask/label/627#xpointer(start-point(string-range(//BODY/DIV[1]/P[1]/text()[1],'',0))/range-to(string-range(//BODY/DIV[1]/P[1]/text()[1],'',2)))\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"predicate\": \"http://edukg.org/knowledge/0.1/property/common#source\",\n" +
                "                \"predicateLabel\": \"出处\",\n" +
                "                \"object\": \"http://kb.cs.tsinghua.edu.cn/apibztask/label/242#xpointer(start-point(string-range(//BODY/TABLE[2]/TBODY[1]/TR[7]/TD[1]/text()[1],'',0))/range-to(string-range(//BODY/TABLE[2]/TBODY[1]/TR[7]/TD[1]/text()[1],'',2)))\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"predicate\": \"http://edukg.org/knowledge/0.1/property/common#source\",\n" +
                "                \"predicateLabel\": \"出处\",\n" +
                "                \"object\": \"http://kb.cs.tsinghua.edu.cn/apibztask/label/1937#xpointer(start-point(string-range(//BODY/P[14]/text()[1],'',2))/range-to(string-range(//BODY/P[14]/text()[1],'',4)))\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"predicate\": \"http://edukg.org/knowledge/0.1/property/chinese#jiguan-65e36c3f035691e0192e39722c8350f0\",\n" +
                "                \"predicateLabel\": \"籍贯\",\n" +
                "                \"object\": \"陇西成纪\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"predicate\": \"http://www.w3.org/2000/01/rdf-schema#label\",\n" +
                "                \"predicateLabel\": \"名称\",\n" +
                "                \"object\": \"李白\"\n" +
                "            }\n" +
                "        ],\n" +
                "        \"label\": \"李白\",\n" +
                "        \"content\": [\n" +
                "            {\n" +
                "                \"predicate\": \"http://edukg.org/knowledge/0.1/property/chinese#zhuyaozuopin-fafe2c6db74ec6e4c778dd322da704c6\",\n" +
                "                \"predicate_label\": \"主要作品\",\n" +
                "                \"object_label\": \"《李太白全集》（收近千首诗）\",\n" +
                "                \"object\": \"http://edukg.org/knowledge/0.1/instance/chinese#litaibaiquanjishoujinqianshoushi-319f39fdcd5ad28d07a46f28e67ddd82\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"predicate\": \"http://edukg.org/knowledge/0.1/property/chinese#zuozhe-2d543e422496f0fd679ad715f58bce47\",\n" +
                "                \"subject_label\": \"蜀道难\",\n" +
                "                \"subject\": \"http://edukg.org/knowledge/0.1/instance/chinese#shudaonan-a078606eb28e5d1b8d1cc5e9cb1466a9\",\n" +
                "                \"predicate_label\": \"作者\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"predicate\": \"http://edukg.org/knowledge/0.1/property/chinese#zhuyaozuopin-fafe2c6db74ec6e4c778dd322da704c6\",\n" +
                "                \"predicate_label\": \"主要作品\",\n" +
                "                \"object_label\": \"《月下独酌》\",\n" +
                "                \"object\": \"http://edukg.org/knowledge/0.1/instance/chinese#yuexiaduzhuo-e00172598a60aa3494dd3bce1c2ab0ac\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"predicate\": \"http://edukg.org/knowledge/0.1/property/chinese#zhuyaozuopin-fafe2c6db74ec6e4c778dd322da704c6\",\n" +
                "                \"predicate_label\": \"主要作品\",\n" +
                "                \"object_label\": \"《望天门山》\",\n" +
                "                \"object\": \"http://edukg.org/knowledge/0.1/instance/chinese#wangtianmenshan-f88a474215a7265bcd3fc0533870cead\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"predicate\": \"http://edukg.org/knowledge/0.1/property/chinese#zhuyaozuopin-fafe2c6db74ec6e4c778dd322da704c6\",\n" +
                "                \"predicate_label\": \"主要作品\",\n" +
                "                \"object_label\": \"《渡荆门送别》\",\n" +
                "                \"object\": \"http://edukg.org/knowledge/0.1/instance/chinese#dujingmensongbie-965a4cb034845910f623f46060aaa084\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"predicate\": \"http://edukg.org/knowledge/0.1/property/chinese#zhuyaozuopin-fafe2c6db74ec6e4c778dd322da704c6\",\n" +
                "                \"predicate_label\": \"主要作品\",\n" +
                "                \"object_label\": \"《李太白全集》\",\n" +
                "                \"object\": \"http://edukg.org/knowledge/0.1/instance/chinese#litaibaiquanji-1fb23cf04b2a4b5f4d4c0e643a8cc231\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"predicate\": \"http://edukg.org/knowledge/0.1/property/chinese#zhuyaozuopin-fafe2c6db74ec6e4c778dd322da704c6\",\n" +
                "                \"predicate_label\": \"主要作品\",\n" +
                "                \"object_label\": \"《越中览古》\",\n" +
                "                \"object\": \"http://edukg.org/knowledge/0.1/instance/chinese#yuezhonglangu-1a1a2686ca989cbb1c7897a1c3fb9a4c\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"predicate\": \"http://edukg.org/knowledge/0.1/property/common#includes\",\n" +
                "                \"subject_label\": \"李杜\",\n" +
                "                \"subject\": \"http://edukg.org/knowledge/0.1/instance/chinese#lidu-f8c45e21c52d50f470b32580046a82ec\",\n" +
                "                \"predicate_label\": \"包含\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"predicate\": \"http://edukg.org/knowledge/0.1/property/chinese#zhuyaozuopin-fafe2c6db74ec6e4c778dd322da704c6\",\n" +
                "                \"predicate_label\": \"主要作品\",\n" +
                "                \"object_label\": \"《春夜宴从弟桃花园序》\",\n" +
                "                \"object\": \"http://edukg.org/knowledge/0.1/instance/chinese#chunyeyancongditaohuayuanxu-3c4aa165583223b4c2085266e990f664\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"predicate\": \"http://edukg.org/knowledge/0.1/property/chinese#zhuyaozuopin-fafe2c6db74ec6e4c778dd322da704c6\",\n" +
                "                \"predicate_label\": \"主要作品\",\n" +
                "                \"object_label\": \"《梦游天姥吟留别》\",\n" +
                "                \"object\": \"http://edukg.org/knowledge/0.1/instance/chinese#mengyoutianlaoyinliubie-b3e595a541f1290b06408224548e96bd\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"predicate\": \"http://edukg.org/knowledge/0.1/property/chinese#zhuyaozuopin-fafe2c6db74ec6e4c778dd322da704c6\",\n" +
                "                \"predicate_label\": \"主要作品\",\n" +
                "                \"object_label\": \"《宣州谢朓楼饯别校书叔云》\",\n" +
                "                \"object\": \"http://edukg.org/knowledge/0.1/instance/chinese#xuanzhouxietiaoloujianbiexiaoshushuyun-abbd2f0094a0b831ece90746eb4dcb13\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"predicate\": \"http://edukg.org/knowledge/0.1/property/chinese#zuozhe-2d543e422496f0fd679ad715f58bce47\",\n" +
                "                \"subject_label\": \"宣州谢朓楼饯别校书叔云\",\n" +
                "                \"subject\": \"http://edukg.org/knowledge/0.1/instance/chinese#xuanzhouxietiaoloujianbiexiaoshushuyun-3a383941cd6448bb7be545e5ffad5aa0\",\n" +
                "                \"predicate_label\": \"作者\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"predicate\": \"http://edukg.org/knowledge/0.1/property/chinese#zuozhe-2d543e422496f0fd679ad715f58bce47\",\n" +
                "                \"subject_label\": \"行路难（其一）\",\n" +
                "                \"subject\": \"http://edukg.org/knowledge/0.1/instance/chinese#xinglunanqiyi-9eb9225cf7a6b3289116e8b961ff2c18\",\n" +
                "                \"predicate_label\": \"作者\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"predicate\": \"http://edukg.org/knowledge/0.1/property/chinese#zhuyaozuopin-fafe2c6db74ec6e4c778dd322da704c6\",\n" +
                "                \"predicate_label\": \"主要作品\",\n" +
                "                \"object_label\": \"《行路难》\",\n" +
                "                \"object\": \"http://edukg.org/knowledge/0.1/instance/chinese#xinglunan-edf366ed924abe6892e5a49afccfea9e\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"predicate\": \"http://edukg.org/knowledge/0.1/property/chinese#zhuyaozuopin-fafe2c6db74ec6e4c778dd322da704c6\",\n" +
                "                \"predicate_label\": \"主要作品\",\n" +
                "                \"object_label\": \"《蜀道难》\",\n" +
                "                \"object\": \"http://edukg.org/knowledge/0.1/instance/chinese#shudaonan-0f31ec86deca1d459a7268fb1d804e7d\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"predicate\": \"http://edukg.org/knowledge/0.1/property/common#includes\",\n" +
                "                \"subject_label\": \"二李\",\n" +
                "                \"subject\": \"http://edukg.org/knowledge/0.1/instance/chinese#erli-f28b273a18400146499a7922c5c441c2\",\n" +
                "                \"predicate_label\": \"包含\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"predicate\": \"http://edukg.org/knowledge/0.1/property/chinese#zuozhe-2d543e422496f0fd679ad715f58bce47\",\n" +
                "                \"subject_label\": \"《李太白全集》\",\n" +
                "                \"subject\": \"http://edukg.org/knowledge/0.1/instance/chinese#litaibaiquanji-1fb23cf04b2a4b5f4d4c0e643a8cc231\",\n" +
                "                \"predicate_label\": \"作者\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"predicate\": \"http://edukg.org/knowledge/0.1/property/chinese#zhuyaozuopin-fafe2c6db74ec6e4c778dd322da704c6\",\n" +
                "                \"predicate_label\": \"主要作品\",\n" +
                "                \"object_label\": \"《闻王昌龄左迁龙标遥有此寄》\",\n" +
                "                \"object\": \"http://edukg.org/knowledge/0.1/instance/chinese#wenwangchanglingzuoqianlongbiaoyaoyouciji-b47a71c35ab741d91f8b5c16ce6426eb\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"predicate\": \"http://edukg.org/knowledge/0.1/property/chinese#zhuyaozuopin-fafe2c6db74ec6e4c778dd322da704c6\",\n" +
                "                \"predicate_label\": \"主要作品\",\n" +
                "                \"object_label\": \"《将进酒》\",\n" +
                "                \"object\": \"http://edukg.org/knowledge/0.1/instance/chinese#jiangjinjiu-cd00795b38f31bf667769a381cd75d5d\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"predicate\": \"http://edukg.org/knowledge/0.1/property/common#includes\",\n" +
                "                \"subject_label\": \"三李\",\n" +
                "                \"subject\": \"http://edukg.org/knowledge/0.1/instance/chinese#sanli-ee42e4b237352a569287f76f9af0606b\",\n" +
                "                \"predicate_label\": \"包含\"\n" +
                "            }\n" +
                "        ]\n" +
                "    }");
        TextView titleView = (TextView) findViewById(R.id.entityTitle);
        titleView.setText(entityName);

        List<JSONObject> objectList = entityJson.getJSONArray("content").toJavaList(JSONObject.class);
        List<RelationEntity> relationFullList, relationPrevList;
        relationFullList = new ArrayList<>();
        for (JSONObject relationJson : objectList) {
            RelationEntity entity = new RelationEntity();
            entity.setRelationName(relationJson.getString("predicate_label"));
            if(relationJson.getString("object") != null) {
                entity.setSubject(false);
                entity.setTargetName(relationJson.getString("object_label"));
            } else {
                entity.setSubject(true);
                entity.setTargetName(relationJson.getString("subject_label"));
            }
            System.out.println(entity);
            relationFullList.add(entity);
        }
        Collections.sort(relationFullList);
        RelationAdapter relationAdapter;
        ListViewForScrollView relationsView = (ListViewForScrollView) findViewById(R.id.relationsView);
        if(relationFullList.size() >= 5) {
            relationPrevList = relationFullList.subList(0, 5);
            relationAdapter = new RelationAdapter(EntityActivity.this, R.layout.relation_item, relationPrevList);
            relationButton.setBackgroundResource(R.drawable.pulldown);
            relationButton.setOnClickListener(new RelationViewOnClickListener(relationFullList, relationPrevList, false, relationsView, relationButton));
        } else {
            relationButton.setVisibility(View.GONE);
            relationAdapter = new RelationAdapter(EntityActivity.this, R.layout.relation_item, relationFullList);
        }
        relationsView.setAdapter(relationAdapter);

        objectList = entityJson.getJSONArray("property").toJavaList(JSONObject.class);
        List<PropertyEntity> propertyFullList, propertyPrevList;
        propertyFullList = new ArrayList<>();
        for(JSONObject propertyJson : objectList) {
            PropertyEntity entity = new PropertyEntity();
            if(propertyJson.getString("object").contains("http"))
                continue;
            entity.setLabel(propertyJson.getString("predicateLabel"));
            entity.setObject(propertyJson.getString("object"));
            propertyFullList.add(entity);
        }

        PropertyAdapter propertyAdapter;
        ListViewForScrollView propertiesView = (ListViewForScrollView) findViewById(R.id.propertiesView);
        if(propertyFullList.size() >= 5) {
            propertyPrevList = propertyFullList.subList(0,5);
            propertyAdapter = new PropertyAdapter(EntityActivity.this, R.layout.property_item, propertyPrevList);
            propertyButton.setBackgroundResource(R.drawable.pulldown);
            propertyButton.setOnClickListener(new PropertyViewOnClickListener(propertyFullList, propertyPrevList, false, propertiesView, propertyButton));
        } else {
            propertyButton.setVisibility(View.GONE);
            propertyAdapter = new PropertyAdapter(EntityActivity.this, R.layout.property_item, propertyFullList);
        }
        propertiesView.setAdapter(propertyAdapter);
    }
}