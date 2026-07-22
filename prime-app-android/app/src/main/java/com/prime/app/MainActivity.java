package com.prime.app;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

/**
 * PRIME+ Android wrapper.
 *
 * O app é uma casca (WebView) que carrega o sistema hospedado no domínio de
 * produção definido em res/values/strings.xml (app_url). Todo o backend
 * (server.ts, Firestore, distribuição de leads) continua no servidor — o
 * celular apenas exibe a interface, exatamente como o navegador faz.
 *
 * Recursos incluídos para dar "cara de app":
 *  - Pull-to-refresh (puxar pra baixo recarrega)
 *  - Botão voltar do Android navega no histórico da página
 *  - Links externos (mailto:, tel:, whatsapp, outros domínios) abrem fora
 *  - Tela de erro amigável quando está sem internet
 *  - Cookies/localStorage persistem (mantém login do Firebase entre sessões)
 */
public class MainActivity extends AppCompatActivity {

    private WebView webView;
    private SwipeRefreshLayout swipeRefresh;
    private boolean lastLoadFailed = false;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        swipeRefresh = findViewById(R.id.swipeRefresh);
        webView = findViewById(R.id.webView);

        WebSettings s = webView.getSettings();
        s.setJavaScriptEnabled(true);
        s.setDomStorageEnabled(true);            // localStorage / sessionStorage
        s.setDatabaseEnabled(true);
        s.setLoadWithOverviewMode(true);
        s.setUseWideViewPort(true);
        s.setCacheMode(WebSettings.LOAD_DEFAULT);
        s.setMediaPlaybackRequiresUserGesture(false);
        s.setJavaScriptCanOpenWindowsAutomatically(true);
        s.setSupportMultipleWindows(false);

        // Mantém sessão/login do Firebase entre aberturas do app
        CookieManager.getInstance().setAcceptCookie(true);
        CookieManager.getInstance().setAcceptThirdPartyCookies(webView, true);

        final String baseUrl = getString(R.string.app_url);

        webView.setWebChromeClient(new WebChromeClient());
        webView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                Uri uri = request.getUrl();
                String url = uri.toString();
                String scheme = uri.getScheme() == null ? "" : uri.getScheme();

                // Esquemas especiais → abrem app externo (discador, e-mail, WhatsApp...)
                if (scheme.equals("tel") || scheme.equals("mailto")
                        || scheme.equals("sms") || scheme.equals("whatsapp")
                        || scheme.equals("geo") || scheme.equals("intent")) {
                    openExternal(url);
                    return true;
                }

                // Mesmo domínio → carrega dentro do app
                Uri base = Uri.parse(baseUrl);
                if (uri.getHost() != null && base.getHost() != null
                        && uri.getHost().equals(base.getHost())) {
                    return false; // deixa o WebView carregar
                }

                // Outro domínio (ex: link do Google, postimg, make.com) → navegador externo
                openExternal(url);
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, android.graphics.Bitmap favicon) {
                lastLoadFailed = false;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                swipeRefresh.setRefreshing(false);
                if (!lastLoadFailed) {
                    webView.setVisibility(View.VISIBLE);
                    findViewById(R.id.errorView).setVisibility(View.GONE);
                }
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                // Só trata erro da página principal (não de sub-recursos)
                if (request.isForMainFrame()) {
                    lastLoadFailed = true;
                    swipeRefresh.setRefreshing(false);
                    webView.setVisibility(View.GONE);
                    findViewById(R.id.errorView).setVisibility(View.VISIBLE);
                }
            }
        });

        // Pull-to-refresh
        swipeRefresh.setOnRefreshListener(() -> webView.reload());

        // Botão "Tentar novamente" da tela de erro
        findViewById(R.id.retryButton).setOnClickListener(v -> {
            findViewById(R.id.errorView).setVisibility(View.GONE);
            webView.setVisibility(View.VISIBLE);
            webView.loadUrl(baseUrl);
        });

        // Botão voltar do Android → volta no histórico da página, se houver
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (webView.canGoBack()) {
                    webView.goBack();
                } else {
                    setEnabled(false);
                    getOnBackPressedDispatcher().onBackPressed();
                }
            }
        });

        // Primeira carga (ou restaura estado após rotação)
        if (savedInstanceState != null) {
            webView.restoreState(savedInstanceState);
        } else {
            webView.loadUrl(baseUrl);
        }
    }

    private void openExternal(String url) {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
        } catch (Exception ignored) {
            // Sem app para abrir esse esquema — silencioso
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        webView.saveState(outState);
    }
}
