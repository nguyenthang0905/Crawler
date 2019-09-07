package crawl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 
 * @author TienHa
 *
 */
@Component
@Transactional
public class CrawlData {

	@Transactional
	public List<Map<String, Object>> clawerDataScene(String choiceUrl) throws IOException {
		List<Map<String, Object>> listsNew = new ArrayList<>();
		Document doc = Jsoup.connect(choiceUrl).get();
		String strSource = doc.html();
		String value = StringUtils.substringBetween(strSource, "window.$$RPC = ", "}]}};");
		value = value + "}]}}";
		JSONObject jsonObject = new JSONObject(value);
		JSONObject shopsData = jsonObject.getJSONObject("shopsData");
		JSONArray array = shopsData.getJSONArray("shops");
		for (int j = 0; j < array.length(); j++) {
			Map<String, Object> model = new HashMap<String, Object>();
			JSONObject jsonShop = array.getJSONObject(j);
			if (jsonShop.getString("categoryId").equals("gourmet")
					|| jsonShop.getString("categoryId").equals("supermarket")
					|| jsonShop.getString("categoryId").equals("drugstrore")
					|| jsonShop.getString("categoryId").equals("interior")
					|| jsonShop.getString("categoryId").equals("fashion")
					|| jsonShop.getString("categoryId").equals("sports")
					|| jsonShop.getString("categoryId").equals("appliance")) {
			
				String linkUrlImage = jsonShop.getString("logoImg");
				
				String sceneUse = null;
				if (jsonShop.getString("categoryId").equals("gourmet")) {
					sceneUse = "グルメ";
				} else {
					sceneUse = "ショッピング";
				}
				String title = jsonShop.getString("name");
				String content = null;
				try {
					content = jsonShop.getString("description");
				} catch (Exception e) {
					content = null;
				}
				String url;
				try {
					url = jsonShop.getString("url");
				} catch (Exception e) {
					url = null;
				}
				// get deatail url
				String grantAndUse = null;
				String officialSite = null;
				String urlSearch = null;
				String timePromotion = null;
				String levelConditions = null;
				String timingPointLevel = null;
				String objectStore = null;
				String productsOutside = null;
				String otherAttention = null;
				Document detail = null;
				if (StringUtils.isNotBlank(url)) {
					try {
						detail = Jsoup.connect(url).get();
					} catch (Exception e) {
						model.put("CompanyCard", "楽天カード");
						model.put("InfoPromotion", "ポイント");
						model.put("BrandCard1", "VISA");
						model.put("BrandCard2","MasterCard");
						model.put("BrandCard3", "JCB");
						model.put("BrandCard4", "AmericanExpress");
						model.put("SceneUse", sceneUse);
						model.put("UrlImage",linkUrlImage);
						model.put("NamePromotion",title);
						model.put("ContentPromotion", content);
						model.put("GrantAndUse", grantAndUse);
						model.put("UrlSite", url);
						model.put("OfficialSite", officialSite);
						model.put("UrlSearch", urlSearch);
						model.put("TimePromotion", timePromotion);
						model.put("LevelConditions", levelConditions);
						model.put("TimingPointLevel", timingPointLevel);
						model.put("ObjectStore", objectStore);
						model.put("ProductsOutside", productsOutside);
						model.put("OtherAttention", otherAttention);
						listsNew.add(model);
						continue;
					}

					if (detail.getElementsByClass("partnerInfoUnit2").first() != null) {
						grantAndUse = detail.getElementsByClass("partnerInfoUnit2").first().text();
					}
					if (detail.getElementsByClass("icon-btn_partner1").first() != null) {
						officialSite = detail.getElementsByClass("icon-btn_partner1").attr("href");
					}
					if (detail.getElementsByClass("icon-btn_partner2").first() != null) {
						urlSearch = detail.getElementsByClass("icon-btn_partner2").attr("href");
					}
					if (detail.getElementsByClass("partnerCampTable").first() != null) {
						Map<String, String> mapTables = new HashMap<>();
						Elements elementsTable = detail.getElementsByClass("partnerCampTable").first()
								.getElementsByTag("tr");
						for (Element item : elementsTable) {
							String key = item.getElementsByTag("th").first().text();
							String valueDatas = item.getElementsByTag("td").first().text();
							mapTables.put(key, valueDatas);
						}
						
						for (Map.Entry<String, String> entry : mapTables.entrySet()) {
							if ("期間".equals(entry.getKey())) {
								timePromotion = entry.getValue();
							}
							if ("付与条件".equals(entry.getKey())) {
								levelConditions = entry.getValue();
							}
						}								
					}
					
					if (detail.getElementsByClass("partnerPointTable").first() != null) {
						Map<String, String> partnerPointTable = new HashMap<String, String>();
						Elements elementTable = detail.getElementsByClass("partnerPointTable").first()
								.getElementsByTag("tr");
						for (Element item : elementTable) {
							String key = item.getElementsByTag("th").first().text();
							String valuesData = item.getElementsByTag("td").first().text();
							partnerPointTable.put(key, valuesData);
						}

						for (Map.Entry<String, String> entry : partnerPointTable.entrySet()) {
							if ("ポイント付与タイミング".equals(entry.getKey())) {
								timingPointLevel = entry.getValue();
							}
							if ("対象店舗".equals(entry.getKey())) {
								objectStore = entry.getValue();
							}
							if("対象外商品".equals(entry.getKey())){
								productsOutside = entry.getValue();
							}
							if ("その他注意事項".equals(entry.getKey())) {
								otherAttention = entry.getValue();
							}
						}

					}
				}
				model.put("CompanyCard", "楽天カード");
				model.put("InfoPromotion", "ポイント");
				model.put("BrandCard1", "VISA");
				model.put("BrandCard2","MasterCard");
				model.put("BrandCard3", "JCB");
				model.put("BrandCard4", "AmericanExpress");
				model.put("SceneUse", sceneUse);
				model.put("UrlImage",linkUrlImage);
				model.put("NamePromotion",title);
				model.put("ContentPromotion", content);
				model.put("GrantAndUse", grantAndUse);
				model.put("UrlSite", url);
				model.put("OfficialSite", officialSite);
				model.put("UrlSearch", urlSearch);
				model.put("TimePromotion", timePromotion);
				model.put("LevelConditions", levelConditions);
				model.put("TimingPointLevel", timingPointLevel);
				model.put("ObjectStore", objectStore);
				model.put("ProductsOutside", productsOutside);
				model.put("OtherAttention", otherAttention);
				listsNew.add(model);
			}
		}
		return listsNew;
	}
}
