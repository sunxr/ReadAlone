package com.github.baby.owspace.util.tool;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.widget.LinearLayout;

import com.github.baby.owspace.util.PaintViewUtil;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.Iterator;

public class AnalysizeHTML {

    public String HTML_STRING = "string";
    public String HTML_URL = "url";

    private Thread strThread;
    private Thread urlThread;

    private Document doc;

    private int wordsLength;
    private int forwardLen;

    private PaintViewUtil paintViewUtil;
    private Activity context;
    private SpannableStringBuilder ssb;
    private int viewType = -1;
    private LinearLayout fuView;
    private int space = 10;

    private Handler handler = new Handler() {
        public void handleMessage(Message message) {
            parseDocument(doc);
        }
    };

    private void loadHtmlString(final String paramString) {
        strThread = new Thread(new Runnable() {
            @Override
            public void run() {
                doc = Jsoup.parseBodyFragment(paramString);
                handler.sendEmptyMessage(0);
            }
        });
        strThread.start();
    }

    private void loadHtmlUrl(final String paramString) {
        urlThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    doc = Jsoup.connect(paramString).get();
                    handler.sendEmptyMessage(0);
                    return;
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        urlThread.start();
    }

    private void parseDocument(Document document) {
        wordsLength = forwardLen;
        paintViewUtil = new PaintViewUtil(context);
        Iterator localIterator = document.getAllElements().iterator();
        while (localIterator.hasNext()) {
            Element localElement = (Element) localIterator.next();
            if ((localElement.nodeName().matches("p[1-9]?[0-9]?")) || (localElement.nodeName().matches("h[1-9]?[0-9]?")) || (localElement.nodeName().matches("poetry")) || (localElement.nodeName().matches("block"))) {
                parseChildOfPH(localElement);
            }
        }
    }

    private void parseChildOfPH(Element paramElement) {
        String str1 = paramElement.text().replaceAll("br;", "\n");
        if (!TextUtils.isEmpty(str1)) {
            ssb = new SpannableStringBuilder("\n" + str1);
            if (paramElement.nodeName().equals("h1")) {
                viewType = 1;
            } else if (paramElement.nodeName().equals("h2")) {
                viewType = 2;
            } else if (paramElement.nodeName().equals("h3")) {
                viewType = 3;
            } else if (paramElement.nodeName().equals("h4")) {
                viewType = 4;
            } else if (paramElement.nodeName().equals("h5")) {
                viewType = 5;
            } else if (paramElement.nodeName().equals("h6")) {
                viewType = 6;
            } else if (paramElement.nodeName().equals("block")) {
                viewType = 7;
            } else if (paramElement.nodeName().equals("poetry")) {
                viewType = 8;
            } else if (paramElement.nodeName().equals("hr")) {
                this.viewType = 10;
            } else {
                viewType = 0;
                if (paramElement.nodeName().contains("strong")){
                    viewType=11;
                }
                ssb = new SpannableStringBuilder("\n" + setFirstLineSpace(str1, 2));
            }
            paintViewUtil.addTypeView(context, this.fuView, this.viewType, ssb, null, null, null, this.wordsLength, 4 * space);
        }
    }

    private String setFirstLineSpace(String str,int paramInt) {
        for (int i = paramInt; i >= 0; i--) {
            str = "  " + str;
        }
        return str;
    }

    public void loadHtml(Activity paramActivity, String content, String type, LinearLayout paramLinearLayout, int paramInt) {
        this.context = paramActivity;
        this.fuView = paramLinearLayout;
        this.forwardLen = paramInt;
        String str = content.replaceAll("<br/>", "br;");
        if (type.equals(this.HTML_URL)) {
            loadHtmlUrl(str);
        } else if (type.equals(this.HTML_STRING)) {
            loadHtmlString(str);
        }

    }
}
