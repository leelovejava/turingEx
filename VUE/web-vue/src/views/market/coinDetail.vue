/* eslint-disable vue/valid-v-bind */
<template>
  <div id="__APP">
    <div class="css-tq0shg">
      <div class="css-14rvj6w">
        <div class="css-ovbogu">
          <div id="header_global_js_wxgy34nj" class="css-1aac2e"></div>
        </div>
      </div>
      <main class="css-1wr4jig">
        <section class="css-1vpbfxv">
          <div class="css-pcu8qt">
            <div class="css-29t16p">
              <div class="css-9uk0ol">
                <div class="css-3wramu">
                  <img
                    :src="handleSymbolImg(moneyInfo.symbol)"
                    class="css-j99ac6"
                  />
                  <h1 class="css-yf08w5">
                    <span class="css-azwkv"
                      >{{ itemName }} {{ $t("message.hangqing.jiage") }}</span
                    ><span class="css-8j8qfu"
                      >(<!-- -->{{ itemName }}
                      <!-- -->)</span
                    >
                  </h1>
                </div>
                <div class="css-1lponck">
                  <div class="css-11lfhgp">
                    {{ $t("message.hangqing.bizhong") }}
                  </div>
                  <div class="css-16jlve5">
                    <div class="bn-input-md css-1vd5j1n">
                      <input value="USD - $" class="bn-sdd-input css-rivkf9" />
                      <div class="bn-input-suffix css-vurnku">
                        <div class="css-13c2b5p">
                          <svg
                            xmlns="http://www.w3.org/2000/svg"
                            viewBox="0 0 24 24"
                            fill="none"
                            class="css-1nlwvj5"
                          >
                            <path
                              d="M16.5 8.49v2.25L12 15.51l-4.5-4.77V8.49h9z"
                              fill="currentColor"
                            ></path>
                          </svg>
                        </div>
                      </div>
                    </div>
                    <div class="bn-sdd-dropdown closing css-1tsr9u1">
                      <div class="bn-sdd-innerInputContainer css-16vu25q">
                        <div class="bn-input-md css-1ra7cyk">
                          <div class="bn-input-prefix css-vurnku">
                            <svg
                              xmlns="http://www.w3.org/2000/svg"
                              viewBox="0 0 24 24"
                              fill="none"
                              class="bn-sdd-icon css-1d886ym"
                            >
                              <path
                                fill-rule="evenodd"
                                clip-rule="evenodd"
                                d="M11 6a5 5 0 110 10 5 5 0 010-10zm0-3a8 8 0 017.021 11.838l3.07 3.07-1.59 1.591-1.591 1.591-3.07-3.07A8 8 0 1111 3z"
                                fill="currentColor"
                              ></path>
                            </svg>
                          </div>
                          <input
                            :placeholder="$t('message.hangqing.sousuo')"
                            class="css-jl5e70"
                            value=""
                          />
                          <div class="bn-input-suffix css-vurnku">
                            <svg
                              xmlns="http://www.w3.org/2000/svg"
                              viewBox="0 0 24 24"
                              fill="none"
                              class="bn-sdd-icon css-1cv87yd"
                            >
                              <path
                                fill-rule="evenodd"
                                clip-rule="evenodd"
                                d="M21 12a9 9 0 11-18 0 9 9 0 0118 0zm-7.233 0l3.006 3.005-1.768 1.768L12 13.767l-3.005 3.005-1.768-1.768 3.005-3.005-3.005-3.005 1.768-1.767L12 10.23l3.005-3.005 1.768 1.767L13.767 12z"
                                fill="currentColor"
                              ></path>
                            </svg>
                          </div>
                        </div>
                      </div>
                      <ul role="listbox" class="bn-sdd-list css-2rl2kr">
                        <li
                          role="option"
                          id="USD_USD"
                          title="USD_USD"
                          class="bn-sdd-option-item css-aw2a3w"
                        >
                          <div class="bn-sdd-option css-4cffwv">
                            <div class="css-1pysja1">USD - $</div>
                          </div>
                        </li>
                        <li
                          role="option"
                          id="EUR_USD"
                          title="EUR_USD"
                          class="bn-sdd-option-item css-1ysb0aq"
                        >
                          <div class="bn-sdd-option css-4cffwv">
                            <div class="css-1pysja1">EUR - €</div>
                          </div>
                        </li>
                        <li
                          role="option"
                          id="INR_USD"
                          title="INR_USD"
                          class="bn-sdd-option-item css-1ysb0aq"
                        >
                          <div class="bn-sdd-option css-4cffwv">
                            <div class="css-1pysja1">INR - ₹</div>
                          </div>
                        </li>
                        <li
                          role="option"
                          id="BRL_USD"
                          title="BRL_USD"
                          class="bn-sdd-option-item css-1ysb0aq"
                        >
                          <div class="bn-sdd-option css-4cffwv">
                            <div class="css-1pysja1">BRL - R$</div>
                          </div>
                        </li>
                        <li
                          role="option"
                          id="PEN_USD"
                          title="PEN_USD"
                          class="bn-sdd-option-item css-1ysb0aq"
                        >
                          <div class="bn-sdd-option css-4cffwv">
                            <div class="css-1pysja1">PEN - S/.</div>
                          </div>
                        </li>
                        <li
                          role="option"
                          id="CHF_USD"
                          title="CHF_USD"
                          class="bn-sdd-option-item css-1ysb0aq"
                        >
                          <div class="bn-sdd-option css-4cffwv">
                            <div class="css-1pysja1">CHF - CHF</div>
                          </div>
                        </li>
                        <li
                          role="option"
                          id="KES_USD"
                          title="KES_USD"
                          class="bn-sdd-option-item css-1ysb0aq"
                        >
                          <div class="bn-sdd-option css-4cffwv">
                            <div class="css-1pysja1">KES - KSh</div>
                          </div>
                        </li>
                        <li
                          role="option"
                          id="NGN_USD"
                          title="NGN_USD"
                          class="bn-sdd-option-item css-1ysb0aq"
                        >
                          <div class="bn-sdd-option css-4cffwv">
                            <div class="css-1pysja1">NGN - ₦</div>
                          </div>
                        </li>
                        <li
                          role="option"
                          id="CNY_USD"
                          title="CNY_USD"
                          class="bn-sdd-option-item css-1ysb0aq"
                        >
                          <div class="bn-sdd-option css-4cffwv">
                            <div class="css-1pysja1">CNY - ¥</div>
                          </div>
                        </li>
                        <li
                          role="option"
                          id="PHP_USD"
                          title="PHP_USD"
                          class="bn-sdd-option-item css-1ysb0aq"
                        >
                          <div class="bn-sdd-option css-4cffwv">
                            <div class="css-1pysja1">PHP - ₱</div>
                          </div>
                        </li>
                        <li
                          role="option"
                          id="UAH_USD"
                          title="UAH_USD"
                          class="bn-sdd-option-item css-1ysb0aq"
                        >
                          <div class="bn-sdd-option css-4cffwv">
                            <div class="css-1pysja1">UAH - ₴</div>
                          </div>
                        </li>
                        <li
                          role="option"
                          id="RUB_USD"
                          title="RUB_USD"
                          class="bn-sdd-option-item css-1ysb0aq"
                        >
                          <div class="bn-sdd-option css-4cffwv">
                            <div class="css-1pysja1">RUB - ₽</div>
                          </div>
                        </li>
                        <li
                          role="option"
                          id="TWD_USD"
                          title="TWD_USD"
                          class="bn-sdd-option-item css-1ysb0aq"
                        >
                          <div class="bn-sdd-option css-4cffwv">
                            <div class="css-1pysja1">TWD - NT$</div>
                          </div>
                        </li>
                        <li
                          role="option"
                          id="BGN_USD"
                          title="BGN_USD"
                          class="bn-sdd-option-item css-1ysb0aq"
                        >
                          <div class="bn-sdd-option css-4cffwv">
                            <div class="css-1pysja1">BGN - лв</div>
                          </div>
                        </li>
                        <li
                          role="option"
                          id="SAR_USD"
                          title="SAR_USD"
                          class="bn-sdd-option-item css-1ysb0aq"
                        >
                          <div class="bn-sdd-option css-4cffwv">
                            <div class="css-1pysja1">SAR - ر.س</div>
                          </div>
                        </li>
                        <li
                          role="option"
                          id="COP_USD"
                          title="COP_USD"
                          class="bn-sdd-option-item css-1ysb0aq"
                        >
                          <div class="bn-sdd-option css-4cffwv">
                            <div class="css-1pysja1">COP - COL$</div>
                          </div>
                        </li>
                        <li
                          role="option"
                          id="UGX_USD"
                          title="UGX_USD"
                          class="bn-sdd-option-item css-1ysb0aq"
                        >
                          <div class="bn-sdd-option css-4cffwv">
                            <div class="css-1pysja1">UGX - USh</div>
                          </div>
                        </li>
                        <li
                          role="option"
                          id="ZAR_USD"
                          title="ZAR_USD"
                          class="bn-sdd-option-item css-1ysb0aq"
                        >
                          <div class="bn-sdd-option css-4cffwv">
                            <div class="css-1pysja1">ZAR - R</div>
                          </div>
                        </li>
                        <li
                          role="option"
                          id="DKK_USD"
                          title="DKK_USD"
                          class="bn-sdd-option-item css-1ysb0aq"
                        >
                          <div class="bn-sdd-option css-4cffwv">
                            <div class="css-1pysja1">DKK - KR</div>
                          </div>
                        </li>
                        <li
                          role="option"
                          id="NZD_USD"
                          title="NZD_USD"
                          class="bn-sdd-option-item css-1ysb0aq"
                        >
                          <div class="bn-sdd-option css-4cffwv">
                            <div class="css-1pysja1">NZD - NZ$</div>
                          </div>
                        </li>
                        <li
                          role="option"
                          id="KZT_USD"
                          title="KZT_USD"
                          class="bn-sdd-option-item css-1ysb0aq"
                        >
                          <div class="bn-sdd-option css-4cffwv">
                            <div class="css-1pysja1">KZT - ₸</div>
                          </div>
                        </li>
                        <li
                          role="option"
                          id="MAD_USD"
                          title="MAD_USD"
                          class="bn-sdd-option-item css-1ysb0aq"
                        >
                          <div class="bn-sdd-option css-4cffwv">
                            <div class="css-1pysja1">MAD - م.د.</div>
                          </div>
                        </li>
                        <li
                          role="option"
                          id="THB_USD"
                          title="THB_USD"
                          class="bn-sdd-option-item css-1ysb0aq"
                        >
                          <div class="bn-sdd-option css-4cffwv">
                            <div class="css-1pysja1">THB - ฿</div>
                          </div>
                        </li>
                        <li
                          role="option"
                          id="PKR_USD"
                          title="PKR_USD"
                          class="bn-sdd-option-item css-1ysb0aq"
                        >
                          <div class="bn-sdd-option css-4cffwv">
                            <div class="css-1pysja1">PKR - ₨</div>
                          </div>
                        </li>
                        <li
                          role="option"
                          id="BOB_USD"
                          title="BOB_USD"
                          class="bn-sdd-option-item css-1ysb0aq"
                        >
                          <div class="bn-sdd-option css-4cffwv">
                            <div class="css-1pysja1">BOB - $b</div>
                          </div>
                        </li>
                        <li
                          role="option"
                          id="TRY_USD"
                          title="TRY_USD"
                          class="bn-sdd-option-item css-1ysb0aq"
                        >
                          <div class="bn-sdd-option css-4cffwv">
                            <div class="css-1pysja1">TRY - ₺</div>
                          </div>
                        </li>
                        <li
                          role="option"
                          id="ARS_USD"
                          title="ARS_USD"
                          class="bn-sdd-option-item css-1ysb0aq"
                        >
                          <div class="bn-sdd-option css-4cffwv">
                            <div class="css-1pysja1">ARS - ARS$</div>
                          </div>
                        </li>
                        <li
                          role="option"
                          id="BDT_USD"
                          title="BDT_USD"
                          class="bn-sdd-option-item css-1ysb0aq"
                        >
                          <div class="bn-sdd-option css-4cffwv">
                            <div class="css-1pysja1">BDT - ৳</div>
                          </div>
                        </li>
                        <li
                          role="option"
                          id="MXN_USD"
                          title="MXN_USD"
                          class="bn-sdd-option-item css-1ysb0aq"
                        >
                          <div class="bn-sdd-option css-4cffwv">
                            <div class="css-1pysja1">MXN - Mex$</div>
                          </div>
                        </li>
                        <li
                          role="option"
                          id="SEK_USD"
                          title="SEK_USD"
                          class="bn-sdd-option-item css-1ysb0aq"
                        >
                          <div class="bn-sdd-option css-4cffwv">
                            <div class="css-1pysja1">SEK - kr</div>
                          </div>
                        </li>
                        <li
                          role="option"
                          id="HUF_USD"
                          title="HUF_USD"
                          class="bn-sdd-option-item css-1ysb0aq"
                        >
                          <div class="bn-sdd-option css-4cffwv">
                            <div class="css-1pysja1">HUF - Ft</div>
                          </div>
                        </li>
                        <li
                          role="option"
                          id="HRK_USD"
                          title="HRK_USD"
                          class="bn-sdd-option-item css-1ysb0aq"
                        >
                          <div class="bn-sdd-option css-4cffwv">
                            <div class="css-1pysja1">HRK - kn</div>
                          </div>
                        </li>
                        <li
                          role="option"
                          id="JPY_USD"
                          title="JPY_USD"
                          class="bn-sdd-option-item css-1ysb0aq"
                        >
                          <div class="bn-sdd-option css-4cffwv">
                            <div class="css-1pysja1">JPY - ¥</div>
                          </div>
                        </li>
                        <li
                          role="option"
                          id="AED_USD"
                          title="AED_USD"
                          class="bn-sdd-option-item css-1ysb0aq"
                        >
                          <div class="bn-sdd-option css-4cffwv">
                            <div class="css-1pysja1">AED - د.إ</div>
                          </div>
                        </li>
                        <li
                          role="option"
                          id="CAD_USD"
                          title="CAD_USD"
                          class="bn-sdd-option-item css-1ysb0aq"
                        >
                          <div class="bn-sdd-option css-4cffwv">
                            <div class="css-1pysja1">CAD - C$</div>
                          </div>
                        </li>
                        <li
                          role="option"
                          id="AUD_USD"
                          title="AUD_USD"
                          class="bn-sdd-option-item css-1ysb0aq"
                        >
                          <div class="bn-sdd-option css-4cffwv">
                            <div class="css-1pysja1">AUD - A$</div>
                          </div>
                        </li>
                        <li
                          role="option"
                          id="EGP_USD"
                          title="EGP_USD"
                          class="bn-sdd-option-item css-1ysb0aq"
                        >
                          <div class="bn-sdd-option css-4cffwv">
                            <div class="css-1pysja1">EGP - £</div>
                          </div>
                        </li>
                        <li
                          role="option"
                          id="MNT_USD"
                          title="MNT_USD"
                          class="bn-sdd-option-item css-1ysb0aq"
                        >
                          <div class="bn-sdd-option css-4cffwv">
                            <div class="css-1pysja1">MNT - ₮</div>
                          </div>
                        </li>
                        <li
                          role="option"
                          id="RON_USD"
                          title="RON_USD"
                          class="bn-sdd-option-item css-1ysb0aq"
                        >
                          <div class="bn-sdd-option css-4cffwv">
                            <div class="css-1pysja1">RON - lei</div>
                          </div>
                        </li>
                        <li
                          role="option"
                          id="IDR_USD"
                          title="IDR_USD"
                          class="bn-sdd-option-item css-1ysb0aq"
                        >
                          <div class="bn-sdd-option css-4cffwv">
                            <div class="css-1pysja1">IDR - Rp</div>
                          </div>
                        </li>
                        <li
                          role="option"
                          id="PLN_USD"
                          title="PLN_USD"
                          class="bn-sdd-option-item css-1ysb0aq"
                        >
                          <div class="bn-sdd-option css-4cffwv">
                            <div class="css-1pysja1">PLN - zł</div>
                          </div>
                        </li>
                        <li
                          role="option"
                          id="HKD_USD"
                          title="HKD_USD"
                          class="bn-sdd-option-item css-1ysb0aq"
                        >
                          <div class="bn-sdd-option css-4cffwv">
                            <div class="css-1pysja1">HKD - HKD</div>
                          </div>
                        </li>
                        <li
                          role="option"
                          id="VND_USD"
                          title="VND_USD"
                          class="bn-sdd-option-item css-1ysb0aq"
                        >
                          <div class="bn-sdd-option css-4cffwv">
                            <div class="css-1pysja1">VND - ₫</div>
                          </div>
                        </li>
                        <li
                          role="option"
                          id="GBP_USD"
                          title="GBP_USD"
                          class="bn-sdd-option-item css-1ysb0aq"
                        >
                          <div class="bn-sdd-option css-4cffwv">
                            <div class="css-1pysja1">GBP - £</div>
                          </div>
                        </li>
                        <li
                          role="option"
                          id="CZK_USD"
                          title="CZK_USD"
                          class="bn-sdd-option-item css-1ysb0aq"
                        >
                          <div class="bn-sdd-option css-4cffwv">
                            <div class="css-1pysja1">CZK - Kč</div>
                          </div>
                        </li>
                        <li
                          role="option"
                          id="VES_USD"
                          title="VES_USD"
                          class="bn-sdd-option-item css-1ysb0aq"
                        >
                          <div class="bn-sdd-option css-4cffwv">
                            <div class="css-1pysja1">VES - Bs</div>
                          </div>
                        </li>
                      </ul>
                    </div>
                  </div>
                </div>
              </div>
              <div class="css-1qjkmaa">
                <div class="css-11lfhgp">
                  {{ $t("message.hangqing.bizhong") }}
                </div>
                <div class="css-16jlve5">
                  <div class="bn-input-md css-1vd5j1n">
                    <input value="USD - $" class="bn-sdd-input css-rivkf9" />
                    <div class="bn-input-suffix css-vurnku">
                      <div class="css-13c2b5p">
                        <svg
                          xmlns="http://www.w3.org/2000/svg"
                          viewBox="0 0 24 24"
                          fill="none"
                          class="css-1nlwvj5"
                        >
                          <path
                            d="M16.5 8.49v2.25L12 15.51l-4.5-4.77V8.49h9z"
                            fill="currentColor"
                          ></path>
                        </svg>
                      </div>
                    </div>
                  </div>
                  <div class="bn-sdd-dropdown closing css-1tsr9u1">
                    <div class="bn-sdd-innerInputContainer css-16vu25q">
                      <div class="bn-input-md css-1ra7cyk">
                        <div class="bn-input-prefix css-vurnku">
                          <svg
                            xmlns="http://www.w3.org/2000/svg"
                            viewBox="0 0 24 24"
                            fill="none"
                            class="bn-sdd-icon css-1d886ym"
                          >
                            <path
                              fill-rule="evenodd"
                              clip-rule="evenodd"
                              d="M11 6a5 5 0 110 10 5 5 0 010-10zm0-3a8 8 0 017.021 11.838l3.07 3.07-1.59 1.591-1.591 1.591-3.07-3.07A8 8 0 1111 3z"
                              fill="currentColor"
                            ></path>
                          </svg>
                        </div>
                        <input placeholder="搜索" class="css-jl5e70" value="" />
                        <div class="bn-input-suffix css-vurnku">
                          <svg
                            xmlns="http://www.w3.org/2000/svg"
                            viewBox="0 0 24 24"
                            fill="none"
                            class="bn-sdd-icon css-1cv87yd"
                          >
                            <path
                              fill-rule="evenodd"
                              clip-rule="evenodd"
                              d="M21 12a9 9 0 11-18 0 9 9 0 0118 0zm-7.233 0l3.006 3.005-1.768 1.768L12 13.767l-3.005 3.005-1.768-1.768 3.005-3.005-3.005-3.005 1.768-1.767L12 10.23l3.005-3.005 1.768 1.767L13.767 12z"
                              fill="currentColor"
                            ></path>
                          </svg>
                        </div>
                      </div>
                    </div>
                    <ul role="listbox" class="bn-sdd-list css-2rl2kr">
                      <li
                        role="option"
                        id="USD_USD"
                        title="USD_USD"
                        class="bn-sdd-option-item css-aw2a3w"
                      >
                        <div class="bn-sdd-option css-4cffwv">
                          <div class="css-1pysja1">USD - $</div>
                        </div>
                      </li>
                      <li
                        role="option"
                        id="EUR_USD"
                        title="EUR_USD"
                        class="bn-sdd-option-item css-1ysb0aq"
                      >
                        <div class="bn-sdd-option css-4cffwv">
                          <div class="css-1pysja1">EUR - €</div>
                        </div>
                      </li>
                      <li
                        role="option"
                        id="INR_USD"
                        title="INR_USD"
                        class="bn-sdd-option-item css-1ysb0aq"
                      >
                        <div class="bn-sdd-option css-4cffwv">
                          <div class="css-1pysja1">INR - ₹</div>
                        </div>
                      </li>
                      <li
                        role="option"
                        id="BRL_USD"
                        title="BRL_USD"
                        class="bn-sdd-option-item css-1ysb0aq"
                      >
                        <div class="bn-sdd-option css-4cffwv">
                          <div class="css-1pysja1">BRL - R$</div>
                        </div>
                      </li>
                      <li
                        role="option"
                        id="PEN_USD"
                        title="PEN_USD"
                        class="bn-sdd-option-item css-1ysb0aq"
                      >
                        <div class="bn-sdd-option css-4cffwv">
                          <div class="css-1pysja1">PEN - S/.</div>
                        </div>
                      </li>
                      <li
                        role="option"
                        id="CHF_USD"
                        title="CHF_USD"
                        class="bn-sdd-option-item css-1ysb0aq"
                      >
                        <div class="bn-sdd-option css-4cffwv">
                          <div class="css-1pysja1">CHF - CHF</div>
                        </div>
                      </li>
                      <li
                        role="option"
                        id="KES_USD"
                        title="KES_USD"
                        class="bn-sdd-option-item css-1ysb0aq"
                      >
                        <div class="bn-sdd-option css-4cffwv">
                          <div class="css-1pysja1">KES - KSh</div>
                        </div>
                      </li>
                      <li
                        role="option"
                        id="NGN_USD"
                        title="NGN_USD"
                        class="bn-sdd-option-item css-1ysb0aq"
                      >
                        <div class="bn-sdd-option css-4cffwv">
                          <div class="css-1pysja1">NGN - ₦</div>
                        </div>
                      </li>
                      <li
                        role="option"
                        id="CNY_USD"
                        title="CNY_USD"
                        class="bn-sdd-option-item css-1ysb0aq"
                      >
                        <div class="bn-sdd-option css-4cffwv">
                          <div class="css-1pysja1">CNY - ¥</div>
                        </div>
                      </li>
                      <li
                        role="option"
                        id="PHP_USD"
                        title="PHP_USD"
                        class="bn-sdd-option-item css-1ysb0aq"
                      >
                        <div class="bn-sdd-option css-4cffwv">
                          <div class="css-1pysja1">PHP - ₱</div>
                        </div>
                      </li>
                      <li
                        role="option"
                        id="UAH_USD"
                        title="UAH_USD"
                        class="bn-sdd-option-item css-1ysb0aq"
                      >
                        <div class="bn-sdd-option css-4cffwv">
                          <div class="css-1pysja1">UAH - ₴</div>
                        </div>
                      </li>
                      <li
                        role="option"
                        id="RUB_USD"
                        title="RUB_USD"
                        class="bn-sdd-option-item css-1ysb0aq"
                      >
                        <div class="bn-sdd-option css-4cffwv">
                          <div class="css-1pysja1">RUB - ₽</div>
                        </div>
                      </li>
                      <li
                        role="option"
                        id="TWD_USD"
                        title="TWD_USD"
                        class="bn-sdd-option-item css-1ysb0aq"
                      >
                        <div class="bn-sdd-option css-4cffwv">
                          <div class="css-1pysja1">TWD - NT$</div>
                        </div>
                      </li>
                      <li
                        role="option"
                        id="BGN_USD"
                        title="BGN_USD"
                        class="bn-sdd-option-item css-1ysb0aq"
                      >
                        <div class="bn-sdd-option css-4cffwv">
                          <div class="css-1pysja1">BGN - лв</div>
                        </div>
                      </li>
                      <li
                        role="option"
                        id="SAR_USD"
                        title="SAR_USD"
                        class="bn-sdd-option-item css-1ysb0aq"
                      >
                        <div class="bn-sdd-option css-4cffwv">
                          <div class="css-1pysja1">SAR - ر.س</div>
                        </div>
                      </li>
                      <li
                        role="option"
                        id="COP_USD"
                        title="COP_USD"
                        class="bn-sdd-option-item css-1ysb0aq"
                      >
                        <div class="bn-sdd-option css-4cffwv">
                          <div class="css-1pysja1">COP - COL$</div>
                        </div>
                      </li>
                      <li
                        role="option"
                        id="UGX_USD"
                        title="UGX_USD"
                        class="bn-sdd-option-item css-1ysb0aq"
                      >
                        <div class="bn-sdd-option css-4cffwv">
                          <div class="css-1pysja1">UGX - USh</div>
                        </div>
                      </li>
                      <li
                        role="option"
                        id="ZAR_USD"
                        title="ZAR_USD"
                        class="bn-sdd-option-item css-1ysb0aq"
                      >
                        <div class="bn-sdd-option css-4cffwv">
                          <div class="css-1pysja1">ZAR - R</div>
                        </div>
                      </li>
                      <li
                        role="option"
                        id="DKK_USD"
                        title="DKK_USD"
                        class="bn-sdd-option-item css-1ysb0aq"
                      >
                        <div class="bn-sdd-option css-4cffwv">
                          <div class="css-1pysja1">DKK - KR</div>
                        </div>
                      </li>
                      <li
                        role="option"
                        id="NZD_USD"
                        title="NZD_USD"
                        class="bn-sdd-option-item css-1ysb0aq"
                      >
                        <div class="bn-sdd-option css-4cffwv">
                          <div class="css-1pysja1">NZD - NZ$</div>
                        </div>
                      </li>
                      <li
                        role="option"
                        id="KZT_USD"
                        title="KZT_USD"
                        class="bn-sdd-option-item css-1ysb0aq"
                      >
                        <div class="bn-sdd-option css-4cffwv">
                          <div class="css-1pysja1">KZT - ₸</div>
                        </div>
                      </li>
                      <li
                        role="option"
                        id="MAD_USD"
                        title="MAD_USD"
                        class="bn-sdd-option-item css-1ysb0aq"
                      >
                        <div class="bn-sdd-option css-4cffwv">
                          <div class="css-1pysja1">MAD - م.د.</div>
                        </div>
                      </li>
                      <li
                        role="option"
                        id="THB_USD"
                        title="THB_USD"
                        class="bn-sdd-option-item css-1ysb0aq"
                      >
                        <div class="bn-sdd-option css-4cffwv">
                          <div class="css-1pysja1">THB - ฿</div>
                        </div>
                      </li>
                      <li
                        role="option"
                        id="PKR_USD"
                        title="PKR_USD"
                        class="bn-sdd-option-item css-1ysb0aq"
                      >
                        <div class="bn-sdd-option css-4cffwv">
                          <div class="css-1pysja1">PKR - ₨</div>
                        </div>
                      </li>
                      <li
                        role="option"
                        id="BOB_USD"
                        title="BOB_USD"
                        class="bn-sdd-option-item css-1ysb0aq"
                      >
                        <div class="bn-sdd-option css-4cffwv">
                          <div class="css-1pysja1">BOB - $b</div>
                        </div>
                      </li>
                      <li
                        role="option"
                        id="TRY_USD"
                        title="TRY_USD"
                        class="bn-sdd-option-item css-1ysb0aq"
                      >
                        <div class="bn-sdd-option css-4cffwv">
                          <div class="css-1pysja1">TRY - ₺</div>
                        </div>
                      </li>
                      <li
                        role="option"
                        id="ARS_USD"
                        title="ARS_USD"
                        class="bn-sdd-option-item css-1ysb0aq"
                      >
                        <div class="bn-sdd-option css-4cffwv">
                          <div class="css-1pysja1">ARS - ARS$</div>
                        </div>
                      </li>
                      <li
                        role="option"
                        id="BDT_USD"
                        title="BDT_USD"
                        class="bn-sdd-option-item css-1ysb0aq"
                      >
                        <div class="bn-sdd-option css-4cffwv">
                          <div class="css-1pysja1">BDT - ৳</div>
                        </div>
                      </li>
                      <li
                        role="option"
                        id="MXN_USD"
                        title="MXN_USD"
                        class="bn-sdd-option-item css-1ysb0aq"
                      >
                        <div class="bn-sdd-option css-4cffwv">
                          <div class="css-1pysja1">MXN - Mex$</div>
                        </div>
                      </li>
                      <li
                        role="option"
                        id="SEK_USD"
                        title="SEK_USD"
                        class="bn-sdd-option-item css-1ysb0aq"
                      >
                        <div class="bn-sdd-option css-4cffwv">
                          <div class="css-1pysja1">SEK - kr</div>
                        </div>
                      </li>
                      <li
                        role="option"
                        id="HUF_USD"
                        title="HUF_USD"
                        class="bn-sdd-option-item css-1ysb0aq"
                      >
                        <div class="bn-sdd-option css-4cffwv">
                          <div class="css-1pysja1">HUF - Ft</div>
                        </div>
                      </li>
                      <li
                        role="option"
                        id="HRK_USD"
                        title="HRK_USD"
                        class="bn-sdd-option-item css-1ysb0aq"
                      >
                        <div class="bn-sdd-option css-4cffwv">
                          <div class="css-1pysja1">HRK - kn</div>
                        </div>
                      </li>
                      <li
                        role="option"
                        id="JPY_USD"
                        title="JPY_USD"
                        class="bn-sdd-option-item css-1ysb0aq"
                      >
                        <div class="bn-sdd-option css-4cffwv">
                          <div class="css-1pysja1">JPY - ¥</div>
                        </div>
                      </li>
                      <li
                        role="option"
                        id="AED_USD"
                        title="AED_USD"
                        class="bn-sdd-option-item css-1ysb0aq"
                      >
                        <div class="bn-sdd-option css-4cffwv">
                          <div class="css-1pysja1">AED - د.إ</div>
                        </div>
                      </li>
                      <li
                        role="option"
                        id="CAD_USD"
                        title="CAD_USD"
                        class="bn-sdd-option-item css-1ysb0aq"
                      >
                        <div class="bn-sdd-option css-4cffwv">
                          <div class="css-1pysja1">CAD - C$</div>
                        </div>
                      </li>
                      <li
                        role="option"
                        id="AUD_USD"
                        title="AUD_USD"
                        class="bn-sdd-option-item css-1ysb0aq"
                      >
                        <div class="bn-sdd-option css-4cffwv">
                          <div class="css-1pysja1">AUD - A$</div>
                        </div>
                      </li>
                      <li
                        role="option"
                        id="EGP_USD"
                        title="EGP_USD"
                        class="bn-sdd-option-item css-1ysb0aq"
                      >
                        <div class="bn-sdd-option css-4cffwv">
                          <div class="css-1pysja1">EGP - £</div>
                        </div>
                      </li>
                      <li
                        role="option"
                        id="MNT_USD"
                        title="MNT_USD"
                        class="bn-sdd-option-item css-1ysb0aq"
                      >
                        <div class="bn-sdd-option css-4cffwv">
                          <div class="css-1pysja1">MNT - ₮</div>
                        </div>
                      </li>
                      <li
                        role="option"
                        id="RON_USD"
                        title="RON_USD"
                        class="bn-sdd-option-item css-1ysb0aq"
                      >
                        <div class="bn-sdd-option css-4cffwv">
                          <div class="css-1pysja1">RON - lei</div>
                        </div>
                      </li>
                      <li
                        role="option"
                        id="IDR_USD"
                        title="IDR_USD"
                        class="bn-sdd-option-item css-1ysb0aq"
                      >
                        <div class="bn-sdd-option css-4cffwv">
                          <div class="css-1pysja1">IDR - Rp</div>
                        </div>
                      </li>
                      <li
                        role="option"
                        id="PLN_USD"
                        title="PLN_USD"
                        class="bn-sdd-option-item css-1ysb0aq"
                      >
                        <div class="bn-sdd-option css-4cffwv">
                          <div class="css-1pysja1">PLN - zł</div>
                        </div>
                      </li>
                      <li
                        role="option"
                        id="HKD_USD"
                        title="HKD_USD"
                        class="bn-sdd-option-item css-1ysb0aq"
                      >
                        <div class="bn-sdd-option css-4cffwv">
                          <div class="css-1pysja1">HKD - HKD</div>
                        </div>
                      </li>
                      <li
                        role="option"
                        id="VND_USD"
                        title="VND_USD"
                        class="bn-sdd-option-item css-1ysb0aq"
                      >
                        <div class="bn-sdd-option css-4cffwv">
                          <div class="css-1pysja1">VND - ₫</div>
                        </div>
                      </li>
                      <li
                        role="option"
                        id="GBP_USD"
                        title="GBP_USD"
                        class="bn-sdd-option-item css-1ysb0aq"
                      >
                        <div class="bn-sdd-option css-4cffwv">
                          <div class="css-1pysja1">GBP - £</div>
                        </div>
                      </li>
                      <li
                        role="option"
                        id="CZK_USD"
                        title="CZK_USD"
                        class="bn-sdd-option-item css-1ysb0aq"
                      >
                        <div class="bn-sdd-option css-4cffwv">
                          <div class="css-1pysja1">CZK - Kč</div>
                        </div>
                      </li>
                      <li
                        role="option"
                        id="VES_USD"
                        title="VES_USD"
                        class="bn-sdd-option-item css-1ysb0aq"
                      >
                        <div class="bn-sdd-option css-4cffwv">
                          <div class="css-1pysja1">VES - Bs</div>
                        </div>
                      </li>
                    </ul>
                  </div>
                </div>
              </div>
            </div>
            <div class="css-871wnf">
              <div class="css-vurnku">
                <div class="css-vurnku">
                  <div class="css-vurnku">
                    <div class="css-ozkomw">
                      BTC{{ $t("message.hangqing.jiagejisuanqi") }}
                    </div>
                    <div class="css-1uqfjfq">
                      <div class="css-oorpkh">
                        {{ $t("message.hangqing.goumai") }}
                      </div>
                      <div class="css-crw4w5">
                        <input
                          type="text"
                          placeholder="0.00"
                          style="font-size: 20px; line-height: 24px"
                          value=""
                        />
                        <div class="css-m850xo">
                          <img
                            src="https://s2.coinmarketcap.com/static/img/coins/64x64/1.png"
                            class="css-8haeji"
                          />
                          <div class="css-1iveb5f">BTC</div>
                        </div>
                      </div>
                    </div>
                    <div class="css-6fqgfw">
                      <div class="css-1tl6ykc">
                        {{ $t("message.hangqing.jiage") }}
                      </div>
                      <div class="css-1tl6ykc">
                        USD<!-- -->
                        <!-- -->$ 0.00
                      </div>
                    </div>
                  </div>
                  <a
                    data-bn-type="button"
                    href="/zh-CN/buy-sell-crypto/calculator/USD/BTC"
                    class="css-v3yxg4"
                    >{{ $t("message.hangqing.goumai") }} BTC</a
                  >
                  <div class="css-12n7o9w">
                    <div class="css-hiryfi">
                      <svg
                        xmlns="http://www.w3.org/2000/svg"
                        viewBox="0 0 48 48"
                        fill="none"
                        class="css-qgqtm8"
                      >
                        <path
                          d="M32.4 15.79c-4.64-4.66-12.17-4.66-16.82-.03-4.65 4.64-4.66 12.17-.02 16.82a11.891 11.891 0 0020.31-8.39c.01-3.15-1.24-6.17-3.47-8.4zM25 30.83h-2v-2h2v2zm0-4.25h-2v-9.05h2v9.05z"
                          fill="url(#warning-filled-m48_svg__paint0_linear)"
                        ></path>
                        <path
                          d="M25.99 37.74c-.58.08-1.17.13-1.75.14l.03 1.88c.66-.01 1.33-.06 1.99-.16l-.27-1.86zm-5.67-.35l-.5 1.81c.64.18 1.3.32 1.96.41l.26-1.87c-.58-.08-1.16-.2-1.72-.35zm9.46-.76c-.54.24-1.09.45-1.65.63l.56 1.79c.64-.19 1.27-.43 1.87-.71l-.78-1.71zm-13.08-.82l-.99 1.6c.56.35 1.15.67 1.76.94l.78-1.71c-.53-.24-1.06-.52-1.55-.83zm16.4-1.32c-.45.38-.92.74-1.4 1.07l1.04 1.56c.55-.37 1.09-.78 1.59-1.21l-1.23-1.42zm-19.43-1.22l-1.4 1.26c.44.49.92.96 1.42 1.4l1.23-1.42c-.44-.39-.86-.8-1.25-1.24zm22.01-1.77c-.31.49-.67.97-1.04 1.42l1.44 1.21c.43-.51.83-1.05 1.19-1.61l-1.59-1.02zm-24.21-1.51l-1.69.81c.28.6.61 1.19.97 1.75l1.58-1.03c-.32-.49-.6-1-.86-1.53zm25.85-2.08c-.16.56-.36 1.12-.6 1.65l1.73.76c.26-.61.49-1.25.68-1.88l-1.81-.53zm-27.03-1.69l-1.85.3c.1.66.25 1.31.44 1.95l1.8-.53c-.16-.56-.29-1.14-.39-1.72zm28.94-2.26l-1.35.04c0 .59-.03 1.18-.11 1.76l1.87.23c.08-.65.12-1.32.12-2.01l-.53-.02zM8.36 22.04c-.08.65-.12 1.31-.12 1.96v.03l1.88-.03c0-.58.03-1.16.1-1.73l-1.86-.23zm30.75-2.54l-1.8.53c.16.56.3 1.14.39 1.72l1.86-.3c-.11-.66-.26-1.32-.45-1.95zM9.54 17.71c-.27.61-.49 1.24-.68 1.88l1.81.53c.16-.56.36-1.12.6-1.66l-1.73-.75zm27.69-2.28l-1.58 1.02c.32.49.61 1.01.86 1.53l1.7-.81c-.29-.6-.62-1.18-.98-1.74zM11.9 13.9c-.43.51-.83 1.05-1.18 1.61l1.58 1.01c.32-.49.67-.97 1.04-1.42l-1.44-1.2zm22.38-1.85l-1.23 1.43c.45.38.87.79 1.26 1.23l1.4-1.26c-.45-.5-.93-.97-1.43-1.4zM15.23 10.9c-.55.37-1.08.78-1.58 1.21l1.23 1.42c.44-.38.91-.74 1.4-1.07l-1.05-1.56zM30.5 9.63l-.78 1.72c.54.24 1.06.52 1.56.83l.99-1.6c-.57-.35-1.16-.67-1.77-.95zm-11.23-.67c-.63.2-1.26.44-1.86.72l.79 1.71c.53-.25 1.08-.46 1.64-.64l-.57-1.79zm6.92-.57l-.26 1.86c.58.08 1.16.2 1.72.35l.5-1.81c-.64-.18-1.3-.31-1.96-.4zm-2.49-.15c-.66.01-1.33.07-1.99.16l.27 1.86c.58-.08 1.17-.13 1.75-.14l-.03-1.88z"
                          fill="url(#warning-filled-m48_svg__paint1_linear)"
                        ></path>
                        <defs>
                          <linearGradient
                            id="warning-filled-m48_svg__paint0_linear"
                            x1="15.57"
                            y1="32.59"
                            x2="32.367"
                            y2="15.793"
                            gradientUnits="userSpaceOnUse"
                          >
                            <stop stop-color="#F0B90B"></stop>
                            <stop offset="0.28" stop-color="#F1BC0F"></stop>
                            <stop offset="0.569" stop-color="#F4C41C"></stop>
                            <stop offset="0.862" stop-color="#F8D230"></stop>
                            <stop offset="0.993" stop-color="#FBDA3C"></stop>
                          </linearGradient>
                          <linearGradient
                            id="warning-filled-m48_svg__paint1_linear"
                            x1="12.854"
                            y1="35.146"
                            x2="35.146"
                            y2="12.854"
                            gradientUnits="userSpaceOnUse"
                          >
                            <stop stop-color="#F0B90B"></stop>
                            <stop offset="0.28" stop-color="#F1BC0F"></stop>
                            <stop offset="0.569" stop-color="#F4C41C"></stop>
                            <stop offset="0.862" stop-color="#F8D230"></stop>
                            <stop offset="0.993" stop-color="#FBDA3C"></stop>
                          </linearGradient>
                        </defs>
                      </svg>
                    </div>
                    <div class="css-16vu25q">
                      <div class="css-6dzks7">
                        {{ $t("message.hangqing.chang1") }}
                      </div>
                      <div class="css-f4auaq">
                        <div class="css-1d6w3z5">Coinbase</div>
                        <div class="css-cfisnl">
                          <div class="css-8fx3ia">
                            <div class="css-14i1rhx">1.99%</div>
                          </div>
                        </div>
                      </div>
                      <div class="css-f4auaq">
                        <div class="css-1d6w3z5">Kraken</div>
                        <div class="css-cfisnl">
                          <div class="css-8fx3ia">
                            <div class="css-1tt3ott">0.26%</div>
                          </div>
                        </div>
                      </div>
                      <div class="css-f4auaq">
                        <div class="css-190n6pw">Binance</div>
                        <div class="css-cfisnl">
                          <div class="css-8fx3ia">
                            <div class="css-1xwypt4">0.1%</div>
                          </div>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
              <div class="css-1wh66rn">
                <div class="css-1267ixm">
                  <div class="css-12ujz79">$ {{ moneyInfo.close }}</div>
                  <div
                    :class="
                      moneyInfo.change_ratio > 0
                        ? 'cssdetailGreen'
                        : 'css-4j2do9'
                    "
                  >
                    {{ moneyInfo.change_ratio }}%
                  </div>
                  <!-- <div  class="css-1upk045">
                    (1天)
                  </div> -->
                </div>
                <div class="css-1tassqi">
                  <button
                    data-bn-type="button"
                    class="css-mat4me"
                    :class="index == timeIndex ? 'css-mat4me' : 'css-mo0upv'"
                    v-for="(item, index) in timeTypeList"
                    :key="index"
                    @click="changeTimeIndex(index)"
                  >
                    <span class="css-ja3m0x">{{ item }}</span>
                  </button>
                </div>
                <div class="css-15owl46">
                  <div class="sc-gKAaRy hydYaP">
                    <div>
                      <div class="css-gnqbje">
                        <div class="sc-jSFjdj jcTaHb">
                          <div class="sc-bdnxRM jvCTkj">
                            <div class="fullscreen">
                              <div>
                                <div
                                  class="floating-tooltip"
                                  style="display: none; left: 439px; top: 188px"
                                >
                                  <div class="date">
                                    <span class="primary"> 2022-03-04 </span>
                                    <span class="sub"> 08:19:00 PM </span>
                                  </div>

                                  <div>
                                    <span class="number">$ 41,605.57</span>
                                  </div>
                                </div>
                                <div>
                                  <div class="chart-wrapper">
                                    <img
                                      class="watermark"
                                      src="https://coinmarketcap.com/public/media/img/logo-square.png"
                                    />
                                    <div class="axis-label right-axis-label">
                                      USD
                                    </div>
                                    <div class="chart">
                                      <div
                                        class="tv-lightweight-charts"
                                        style="
                                          overflow: hidden;
                                          width: 677px;
                                          height: 360px;
                                          user-select: none;
                                          -webkit-tap-highlight-color: transparent;
                                        "
                                      >
                                        <table
                                          cellspacing="0"
                                          style="height: 360px; width: 677px"
                                        >
                                          <tr>
                                            <td style="padding: 0px"></td>
                                            <td
                                              style="
                                                padding: 0px;
                                                position: relative;
                                                width: 625px;
                                                height: 334px;
                                              "
                                            >
                                              <div
                                                style="
                                                  width: 100%;
                                                  height: 100%;
                                                  position: relative;
                                                  overflow: hidden;
                                                "
                                              >
                                                <div
                                                  id="myChart"
                                                  :style="{
                                                    width: '716px',
                                                    height: '334px',
                                                  }"
                                                ></div>
                                              </div>
                                            </td>
                                            <td style="padding: 0px">
                                              <div
                                                style="
                                                  height: 334px;
                                                  overflow: hidden;
                                                  width: 52px;
                                                  left: 0px;
                                                  position: relative;
                                                  min-width: 52px;
                                                  cursor: default;
                                                "
                                              >
                                                <canvas
                                                  width="52"
                                                  height="334"
                                                  style="
                                                    width: 52px;
                                                    height: 334px;
                                                    position: absolute;
                                                    z-index: 1;
                                                    left: 0px;
                                                    top: 0px;
                                                  "
                                                ></canvas
                                                ><canvas
                                                  width="52"
                                                  height="334"
                                                  style="
                                                    width: 52px;
                                                    height: 334px;
                                                    position: absolute;
                                                    z-index: 2;
                                                    left: 0px;
                                                    top: 0px;
                                                  "
                                                ></canvas>
                                              </div>
                                            </td>
                                          </tr>
                                          <tr>
                                            <td style="padding: 0px"></td>
                                            <td
                                              style="
                                                height: 26px;
                                                padding: 0px;
                                                width: 625px;
                                              "
                                            >
                                              <div
                                                style="
                                                  width: 100%;
                                                  height: 100%;
                                                  position: relative;
                                                  overflow: hidden;
                                                "
                                              >
                                                <canvas
                                                  width="625"
                                                  height="26"
                                                  style="
                                                    width: 625px;
                                                    height: 26px;
                                                    position: absolute;
                                                    z-index: 1;
                                                    left: 0px;
                                                    top: 0px;
                                                  "
                                                ></canvas
                                                ><canvas
                                                  width="625"
                                                  height="26"
                                                  style="
                                                    width: 625px;
                                                    height: 26px;
                                                    position: absolute;
                                                    z-index: 2;
                                                    left: 0px;
                                                    top: 0px;
                                                  "
                                                ></canvas>
                                              </div>
                                            </td>
                                            <td style="padding: 0px">
                                              <div
                                                style="
                                                  width: 52px;
                                                  height: 26px;
                                                  overflow: hidden;
                                                  min-width: 52px;
                                                "
                                              >
                                                <canvas
                                                  width="52"
                                                  height="26"
                                                  style="
                                                    width: 52px;
                                                    height: 26px;
                                                  "
                                                ></canvas>
                                              </div>
                                            </td>
                                          </tr>
                                        </table>
                                      </div>
                                    </div>
                                  </div>
                                </div>
                              </div>
                            </div>
                          </div>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
                <div class="css-1e4wwo8">
                  <h2 class="css-q5pm6r">
                    {{ moneyInfo.symbol
                    }}{{ $t("message.hangqing.jiagexinxi") }}
                  </h2>
                  <div class="css-seeeba">
                    <div class="css-vurnku">
                      {{ $t("message.hangqing.chang2") }}
                    </div>
                    <div class="css-1ctijqw">
                      <svg
                        xmlns="http://www.w3.org/2000/svg"
                        viewBox="0 0 24 24"
                        fill="none"
                        class="css-1ii0f6a"
                      >
                        <path
                          fill-rule="evenodd"
                          clip-rule="evenodd"
                          d="M12 4c4.411 0 8 3.589 8 8s-3.589 8-8 8-8-3.589-8-8 3.589-8 8-8zm0-2C6.477 2 2 6.477 2 12s4.477 10 10 10 10-4.477 10-10S17.523 2 12 2zm.997 9.004h-2.009v6h2.009v-6zm-2.009-4h2.009v2.009h-2.009v-2.01z"
                          fill="currentColor"
                        ></path>
                      </svg>
                      <div class="css-1e010nk">
                        <div class="css-7nj4kf">
                          {{ $t("message.hangqing.chang3") }}
                        </div>
                        <div class="css-1083pzh"></div>
                      </div>
                    </div>
                    <svg
                      xmlns="http://www.w3.org/2000/svg"
                      viewBox="0 0 16 16"
                      fill="none"
                      class="css-s7fhjt"
                    >
                      <path
                        d="M8.825 8L3.95 12.95 5 14l6-6-6-6-1.05 1.05L8.825 8z"
                        fill="currentColor"
                      ></path>
                    </svg>
                  </div>
                  <div class="css-x24h4a">
                    <div class="css-1ez451f">
                      {{ $t("message.hangqing.gao") }}: $ 41,027.53
                    </div>
                    <div class="css-x0xnxs">
                      <div class="css-ogwcmj"></div>
                    </div>
                    <div class="css-1ez451f">
                      {{ $t("message.hangqing.di") }}: $ 44,009.46
                    </div>
                  </div>
                  <div class="css-160k37i">
                    <div class="css-13ilkp4">
                      {{ $t("message.hangqing.chang3") }}
                    </div>
                  </div>
                  <div class="css-1qxio5n">
                    <div class="css-1gfbz2s">
                      <div class="css-12a6a97">
                        <div class="css-fqmucm">
                          <div class="css-vurnku">
                            {{ $t("message.hangqing.lishizuigao") }}
                          </div>
                          <div class="css-1ctijqw">
                            <svg
                              xmlns="http://www.w3.org/2000/svg"
                              viewBox="0 0 24 24"
                              fill="none"
                              class="css-1hqfb0o"
                            >
                              <path
                                fill-rule="evenodd"
                                clip-rule="evenodd"
                                d="M12 4c4.411 0 8 3.589 8 8s-3.589 8-8 8-8-3.589-8-8 3.589-8 8-8zm0-2C6.477 2 2 6.477 2 12s4.477 10 10 10 10-4.477 10-10S17.523 2 12 2zm.997 9.004h-2.009v6h2.009v-6zm-2.009-4h2.009v2.009h-2.009v-2.01z"
                                fill="currentColor"
                              ></path>
                            </svg>
                            <div class="css-1e010nk">
                              <div class="css-7nj4kf">
                                {{ $t("message.hangqing.chang4") }}
                              </div>
                              <div class="css-1083pzh"></div>
                            </div>
                          </div>
                        </div>
                        <div class="css-1esqdzc">
                          <div class="css-1o346il">$ 68,789.63</div>
                          <div class="css-vurnku">
                            <svg
                              xmlns="http://www.w3.org/2000/svg"
                              viewBox="0 0 16 16"
                              fill="none"
                              class="css-s7fhjt"
                            >
                              <path
                                d="M8.825 8L3.95 12.95 5 14l6-6-6-6-1.05 1.05L8.825 8z"
                                fill="currentColor"
                              ></path>
                            </svg>
                          </div>
                        </div>
                      </div>
                      <div class="css-jyxwm2">
                        <div class="css-13ilkp4">
                          {{ $t("message.hangqing.chang4") }}
                        </div>
                      </div>
                    </div>
                    <div class="css-1gfbz2s">
                      <div class="css-12a6a97">
                        <div class="css-fqmucm">
                          <div class="css-vurnku">
                            {{ $t("message.hangqing.zhangdie1xiaoshi") }}
                          </div>
                          <div class="css-1ctijqw">
                            <svg
                              xmlns="http://www.w3.org/2000/svg"
                              viewBox="0 0 24 24"
                              fill="none"
                              class="css-1hqfb0o"
                            >
                              <path
                                fill-rule="evenodd"
                                clip-rule="evenodd"
                                d="M12 4c4.411 0 8 3.589 8 8s-3.589 8-8 8-8-3.589-8-8 3.589-8 8-8zm0-2C6.477 2 2 6.477 2 12s4.477 10 10 10 10-4.477 10-10S17.523 2 12 2zm.997 9.004h-2.009v6h2.009v-6zm-2.009-4h2.009v2.009h-2.009v-2.01z"
                                fill="currentColor"
                              ></path>
                            </svg>
                            <div class="css-1e010nk">
                              <div class="css-7nj4kf">
                                {{ $t("message.hangqing.chang5") }}
                              </div>
                              <div class="css-1083pzh"></div>
                            </div>
                          </div>
                        </div>
                        <div class="css-1esqdzc">
                          <div class="css-1q7gaws">+0.57%</div>
                          <div class="css-vurnku">
                            <svg
                              xmlns="http://www.w3.org/2000/svg"
                              viewBox="0 0 16 16"
                              fill="none"
                              class="css-s7fhjt"
                            >
                              <path
                                d="M8.825 8L3.95 12.95 5 14l6-6-6-6-1.05 1.05L8.825 8z"
                                fill="currentColor"
                              ></path>
                            </svg>
                          </div>
                        </div>
                      </div>
                      <div class="css-jyxwm2">
                        <div class="css-13ilkp4">
                          {{ $t("message.hangqing.chang5") }}
                        </div>
                      </div>
                    </div>
                    <div class="css-1gfbz2s">
                      <div class="css-12a6a97">
                        <div class="css-fqmucm">
                          <div class="css-vurnku">
                            {{ $t("message.hangqing.zhangdie24xiaoshi") }}
                          </div>
                          <div class="css-1ctijqw">
                            <svg
                              xmlns="http://www.w3.org/2000/svg"
                              viewBox="0 0 24 24"
                              fill="none"
                              class="css-1hqfb0o"
                            >
                              <path
                                fill-rule="evenodd"
                                clip-rule="evenodd"
                                d="M12 4c4.411 0 8 3.589 8 8s-3.589 8-8 8-8-3.589-8-8 3.589-8 8-8zm0-2C6.477 2 2 6.477 2 12s4.477 10 10 10 10-4.477 10-10S17.523 2 12 2zm.997 9.004h-2.009v6h2.009v-6zm-2.009-4h2.009v2.009h-2.009v-2.01z"
                                fill="currentColor"
                              ></path>
                            </svg>
                            <div class="css-1e010nk">
                              <div class="css-7nj4kf">
                                {{ $t("message.hangqing.chang6") }}
                              </div>
                              <div class="css-1083pzh"></div>
                            </div>
                          </div>
                        </div>
                        <div class="css-1esqdzc">
                          <div class="css-okmmzw">-4.54%</div>
                          <div class="css-vurnku">
                            <svg
                              xmlns="http://www.w3.org/2000/svg"
                              viewBox="0 0 16 16"
                              fill="none"
                              class="css-s7fhjt"
                            >
                              <path
                                d="M8.825 8L3.95 12.95 5 14l6-6-6-6-1.05 1.05L8.825 8z"
                                fill="currentColor"
                              ></path>
                            </svg>
                          </div>
                        </div>
                      </div>
                      <div class="css-jyxwm2">
                        <div class="css-13ilkp4">
                          {{ $t("message.hangqing.chang6") }}
                        </div>
                      </div>
                    </div>
                    <div class="css-r8v1gl">
                      <div class="css-12a6a97">
                        <div class="css-fqmucm">
                          <div class="css-vurnku">
                            {{ $t("message.hangqing.zhangdie7tian") }}
                          </div>
                          <div class="css-1ctijqw">
                            <svg
                              xmlns="http://www.w3.org/2000/svg"
                              viewBox="0 0 24 24"
                              fill="none"
                              class="css-1hqfb0o"
                            >
                              <path
                                fill-rule="evenodd"
                                clip-rule="evenodd"
                                d="M12 4c4.411 0 8 3.589 8 8s-3.589 8-8 8-8-3.589-8-8 3.589-8 8-8zm0-2C6.477 2 2 6.477 2 12s4.477 10 10 10 10-4.477 10-10S17.523 2 12 2zm.997 9.004h-2.009v6h2.009v-6zm-2.009-4h2.009v2.009h-2.009v-2.01z"
                                fill="currentColor"
                              ></path>
                            </svg>
                            <div class="css-1e010nk">
                              <div class="css-7nj4kf">
                                {{ $t("message.hangqing.chang7") }}
                              </div>
                              <div class="css-1083pzh"></div>
                            </div>
                          </div>
                        </div>
                        <div class="css-1esqdzc">
                          <div class="css-1q7gaws">+5.60%</div>
                          <div class="css-vurnku">
                            <svg
                              xmlns="http://www.w3.org/2000/svg"
                              viewBox="0 0 16 16"
                              fill="none"
                              class="css-s7fhjt"
                            >
                              <path
                                d="M8.825 8L3.95 12.95 5 14l6-6-6-6-1.05 1.05L8.825 8z"
                                fill="currentColor"
                              ></path>
                            </svg>
                          </div>
                        </div>
                      </div>
                      <div class="css-jyxwm2">
                        <div class="css-13ilkp4">
                          {{ $t("message.hangqing.chang7") }}
                        </div>
                      </div>
                    </div>
                  </div>
                  <div class="css-z3ryt5">
                    <div class="css-12a6a97">
                      <div class="css-fqmucm">
                        <div class="css-vurnku">
                          {{ $t("message.hangqing.zhangdie7tian") }}
                        </div>
                        <div class="css-1ctijqw">
                          <svg
                            xmlns="http://www.w3.org/2000/svg"
                            viewBox="0 0 24 24"
                            fill="none"
                            class="css-1hqfb0o"
                          >
                            <path
                              fill-rule="evenodd"
                              clip-rule="evenodd"
                              d="M12 4c4.411 0 8 3.589 8 8s-3.589 8-8 8-8-3.589-8-8 3.589-8 8-8zm0-2C6.477 2 2 6.477 2 12s4.477 10 10 10 10-4.477 10-10S17.523 2 12 2zm.997 9.004h-2.009v6h2.009v-6zm-2.009-4h2.009v2.009h-2.009v-2.01z"
                              fill="currentColor"
                            ></path>
                          </svg>
                          <div class="css-1e010nk">
                            <div class="css-7nj4kf">
                              {{ $t("message.hangqing.chang7") }}
                            </div>
                            <div class="css-1083pzh"></div>
                          </div>
                        </div>
                      </div>
                      <div class="css-1esqdzc">
                        <div class="css-1q7gaws">+5.60%</div>
                        <div class="css-vurnku">
                          <svg
                            xmlns="http://www.w3.org/2000/svg"
                            viewBox="0 0 16 16"
                            fill="none"
                            class="css-s7fhjt"
                          >
                            <path
                              d="M8.825 8L3.95 12.95 5 14l6-6-6-6-1.05 1.05L8.825 8z"
                              fill="currentColor"
                            ></path>
                          </svg>
                        </div>
                      </div>
                    </div>
                    <div class="css-jyxwm2">
                      <div class="css-13ilkp4">
                        {{ $t("message.hangqing.chang7") }}
                      </div>
                    </div>
                  </div>
                </div>
                <div class="css-1bkir0r">
                  <h2 class="css-q5pm6r">
                    BTC{{ $t("message.hangqing.shichangxinxi") }}
                  </h2>

                  <div class="css-arolzm">
                    <div class="css-1gfbz2s">
                      <div class="css-12a6a97">
                        <div class="css-fqmucm">
                          <div class="css-vurnku">
                            {{ $t("message.hangqing.remendu") }}
                          </div>
                          <div class="css-1ctijqw">
                            <svg
                              xmlns="http://www.w3.org/2000/svg"
                              viewBox="0 0 24 24"
                              fill="none"
                              class="css-1hqfb0o"
                            >
                              <path
                                fill-rule="evenodd"
                                clip-rule="evenodd"
                                d="M12 4c4.411 0 8 3.589 8 8s-3.589 8-8 8-8-3.589-8-8 3.589-8 8-8zm0-2C6.477 2 2 6.477 2 12s4.477 10 10 10 10-4.477 10-10S17.523 2 12 2zm.997 9.004h-2.009v6h2.009v-6zm-2.009-4h2.009v2.009h-2.009v-2.01z"
                                fill="currentColor"
                              ></path>
                            </svg>
                            <div class="css-1e010nk">
                              <div class="css-7nj4kf">
                                {{ $t("message.hangqing.chang8") }}
                              </div>
                              <div class="css-1083pzh"></div>
                            </div>
                          </div>
                        </div>
                        <div class="css-1esqdzc">
                          <div class="css-1o346il">#1</div>
                          <div class="css-vurnku">
                            <svg
                              xmlns="http://www.w3.org/2000/svg"
                              viewBox="0 0 16 16"
                              fill="none"
                              class="css-s7fhjt"
                            >
                              <path
                                d="M8.825 8L3.95 12.95 5 14l6-6-6-6-1.05 1.05L8.825 8z"
                                fill="currentColor"
                              ></path>
                            </svg>
                          </div>
                        </div>
                      </div>
                      <div class="css-jyxwm2">
                        <div class="css-13ilkp4">
                          {{ $t("message.hangqing.chang9") }}
                        </div>
                      </div>
                    </div>
                    <div class="css-1gfbz2s">
                      <div class="css-12a6a97">
                        <div class="css-fqmucm">
                          <div class="css-vurnku">
                            {{ $t("message.hangqing.shizhi") }}
                          </div>
                          <div class="css-1ctijqw">
                            <svg
                              xmlns="http://www.w3.org/2000/svg"
                              viewBox="0 0 24 24"
                              fill="none"
                              class="css-1hqfb0o"
                            >
                              <path
                                fill-rule="evenodd"
                                clip-rule="evenodd"
                                d="M12 4c4.411 0 8 3.589 8 8s-3.589 8-8 8-8-3.589-8-8 3.589-8 8-8zm0-2C6.477 2 2 6.477 2 12s4.477 10 10 10 10-4.477 10-10S17.523 2 12 2zm.997 9.004h-2.009v6h2.009v-6zm-2.009-4h2.009v2.009h-2.009v-2.01z"
                                fill="currentColor"
                              ></path>
                            </svg>
                            <div class="css-1e010nk">
                              <div class="css-7nj4kf">
                                {{ $t("message.hangqing.chang9") }}
                              </div>
                              <div class="css-1083pzh"></div>
                            </div>
                          </div>
                        </div>
                        <div class="css-1esqdzc">
                          <div class="css-1o346il">$ 790.80B</div>
                          <div class="css-vurnku">
                            <svg
                              xmlns="http://www.w3.org/2000/svg"
                              viewBox="0 0 16 16"
                              fill="none"
                              class="css-s7fhjt"
                            >
                              <path
                                d="M8.825 8L3.95 12.95 5 14l6-6-6-6-1.05 1.05L8.825 8z"
                                fill="currentColor"
                              ></path>
                            </svg>
                          </div>
                        </div>
                      </div>
                      <div class="css-jyxwm2">
                        <div class="css-13ilkp4">
                          {{ $t("message.hangqing.chang9") }}
                        </div>
                      </div>
                    </div>
                    <div class="css-1gfbz2s">
                      <div class="css-12a6a97">
                        <div class="css-fqmucm">
                          <div class="css-vurnku">
                            {{ $t("message.home.chengjiaoliang") }}
                          </div>
                          <div class="css-1ctijqw">
                            <svg
                              xmlns="http://www.w3.org/2000/svg"
                              viewBox="0 0 24 24"
                              fill="none"
                              class="css-1hqfb0o"
                            >
                              <path
                                fill-rule="evenodd"
                                clip-rule="evenodd"
                                d="M12 4c4.411 0 8 3.589 8 8s-3.589 8-8 8-8-3.589-8-8 3.589-8 8-8zm0-2C6.477 2 2 6.477 2 12s4.477 10 10 10 10-4.477 10-10S17.523 2 12 2zm.997 9.004h-2.009v6h2.009v-6zm-2.009-4h2.009v2.009h-2.009v-2.01z"
                                fill="currentColor"
                              ></path>
                            </svg>
                            <div class="css-1e010nk">
                              <div class="css-7nj4kf">
                                {{ $t("message.hangqing.chang10") }}
                              </div>
                              <div class="css-1083pzh"></div>
                            </div>
                          </div>
                        </div>
                        <div class="css-1esqdzc">
                          <div class="css-1o346il">$ 29.44B</div>
                          <div class="css-vurnku">
                            <svg
                              xmlns="http://www.w3.org/2000/svg"
                              viewBox="0 0 16 16"
                              fill="none"
                              class="css-s7fhjt"
                            >
                              <path
                                d="M8.825 8L3.95 12.95 5 14l6-6-6-6-1.05 1.05L8.825 8z"
                                fill="currentColor"
                              ></path>
                            </svg>
                          </div>
                        </div>
                      </div>
                      <div class="css-jyxwm2">
                        <div class="css-13ilkp4">
                          {{ $t("message.hangqing.chang10") }}
                        </div>
                      </div>
                    </div>
                    <div class="css-r8v1gl">
                      <div class="css-12a6a97">
                        <div class="css-fqmucm">
                          <div class="css-vurnku">
                            {{ $t("message.hangqing.liutongshuliang") }}
                          </div>
                          <div class="css-1ctijqw">
                            <svg
                              xmlns="http://www.w3.org/2000/svg"
                              viewBox="0 0 24 24"
                              fill="none"
                              class="css-1hqfb0o"
                            >
                              <path
                                fill-rule="evenodd"
                                clip-rule="evenodd"
                                d="M12 4c4.411 0 8 3.589 8 8s-3.589 8-8 8-8-3.589-8-8 3.589-8 8-8zm0-2C6.477 2 2 6.477 2 12s4.477 10 10 10 10-4.477 10-10S17.523 2 12 2zm.997 9.004h-2.009v6h2.009v-6zm-2.009-4h2.009v2.009h-2.009v-2.01z"
                                fill="currentColor"
                              ></path>
                            </svg>
                            <div class="css-1e010nk">
                              <div class="css-7nj4kf">
                                {{ $t("message.hangqing.chang11") }}
                              </div>
                              <div class="css-1083pzh"></div>
                            </div>
                          </div>
                        </div>
                        <div class="css-1esqdzc">
                          <div class="css-1o346il">$ 18.97M</div>
                          <div class="css-vurnku">
                            <svg
                              xmlns="http://www.w3.org/2000/svg"
                              viewBox="0 0 16 16"
                              fill="none"
                              class="css-s7fhjt"
                            >
                              <path
                                d="M8.825 8L3.95 12.95 5 14l6-6-6-6-1.05 1.05L8.825 8z"
                                fill="currentColor"
                              ></path>
                            </svg>
                          </div>
                        </div>
                      </div>
                      <div class="css-jyxwm2">
                        <div class="css-13ilkp4">
                          {{ $t("message.hangqing.chang11") }}
                        </div>
                      </div>
                    </div>
                  </div>
                  <div class="css-z3ryt5">
                    <div class="css-12a6a97">
                      <div class="css-fqmucm">
                        <div class="css-vurnku">
                          {{ $t("message.hangqing.liutongshuliang") }}
                        </div>
                        <div class="css-1ctijqw">
                          <svg
                            xmlns="http://www.w3.org/2000/svg"
                            viewBox="0 0 24 24"
                            fill="none"
                            class="css-1hqfb0o"
                          >
                            <path
                              fill-rule="evenodd"
                              clip-rule="evenodd"
                              d="M12 4c4.411 0 8 3.589 8 8s-3.589 8-8 8-8-3.589-8-8 3.589-8 8-8zm0-2C6.477 2 2 6.477 2 12s4.477 10 10 10 10-4.477 10-10S17.523 2 12 2zm.997 9.004h-2.009v6h2.009v-6zm-2.009-4h2.009v2.009h-2.009v-2.01z"
                              fill="currentColor"
                            ></path>
                          </svg>
                          <div class="css-1e010nk">
                            <div class="css-7nj4kf">
                              {{ $t("message.hangqing.chang11") }}
                            </div>
                            <div class="css-1083pzh"></div>
                          </div>
                        </div>
                      </div>
                      <div class="css-1esqdzc">
                        <div class="css-1o346il">$ 18.97M</div>
                        <div class="css-vurnku">
                          <svg
                            xmlns="http://www.w3.org/2000/svg"
                            viewBox="0 0 16 16"
                            fill="none"
                            class="css-s7fhjt"
                          >
                            <path
                              d="M8.825 8L3.95 12.95 5 14l6-6-6-6-1.05 1.05L8.825 8z"
                              fill="currentColor"
                            ></path>
                          </svg>
                        </div>
                      </div>
                    </div>
                    <div class="css-jyxwm2">
                      <div class="css-13ilkp4">
                        {{ $t("message.hangqing.chang11") }}
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </section>
      </main>
      <!-- <footer-view></footer-view> -->
    </div>
  </div>
</template>
<script>
import config from "../../config/index.js";
import dataOptionLine from "./DetaillineOption";
import { handleSymbolImg } from "@/utils";
var lineOption = dataOptionLine;

// import enInfo from "../assets/lan/en.js";
// import zhCNInfo from "../assets/lan/zh-CN.js";
// import CNInfo from "../assets/lan/CN.js";
// var enlang = enInfo.exchange;
// var zhCNlang = zhCNInfo.exchange;
// var CNlang = CNInfo.exchange;

export default {
  data() {
    return {
      baseUrl: config.BASE_URL,
      lineShow: true,
      loginShow: false,
      myChart: null,
      option: lineOption,
      echartsData: {},
      itemName: "",
      timeIndex: 0,
      timeTypeList: [
        "Time",
        "1m",
        "5m",
        "15m",
        "30m",
        "1h",
        "4h",
        "1D",
        "1W",
        "1M",
      ],
      moneyInfo: {
        name: "",
        high: 0,
        close: 0,
        amount: 0,
        change_ratio: 0,
        symbol: "btc",
      },
    };
  },
  watch: {
    optionK: {
      handler: function () {
        this.updateEcharts();
      },
      deep: true,
    },
    option: {
      handler: function () {
        // this.updateEchartsLine();
      },
      deep: true,
    },
  },
  created() {
    console.error("craated");
    this.option.xAxis[0].data = null;
    this.option.series[0].data = null;
    this.option.yAxis[0].min = null;
  },
  mounted() {
    console.error("mounted");
    this.itemName = localStorage.getItem("chooseItem");

    this.drawLine();
    this.doCharts(0);
    // this.getLineChartsData();
    this.getChartsData("1mon");
    this.getDetail();
  },
  unmounted() {},

  methods: {
    handleSymbolImg,
    doCharts(index) {
      switch (index) {
        case 0:
          this.getLineChartsData();
          break;
        case 1:
          this.getChartsData("1min");
          break;
        case 2:
          this.getChartsData("5min");
          break;
        case 3:
          this.getChartsData("15min");
          break;
        case 4:
          this.getChartsData("30min");
          break;
        case 5:
          this.getChartsData("60min");
          break;
        case 6:
          this.getChartsData("4hour");
          break;
        case 7:
          this.getChartsData("1day");
          break;
        case 8:
          this.getChartsData("1week");
          break;
        case 9:
          this.getChartsData("1mon");
          break;
        default:
          break;
      }
    },
    changeTimeIndex(index) {
      this.timeIndex = index;
      this.doCharts(index);
    },
    updateEchartsLine() {
      if (this.myChart) {
        this.myChart.setOption(this.option);
      }
    },
    drawLine() {
      this.myChart = this.$echarts.init(document.getElementById("myChart"));
      this.myChart.setOption(this.option);
    },

    changeOption(xData, data, dataZhu) {
      var me = this;

      this.option.xAxis[0].data = xData;
      //this.option.xAxis[1].data = xData;
      this.option.series[0].data = data;
      // this.option.series[1].data = dataZhu;
      this.option.yAxis[0].min = Math.min.apply(null, data);
      //this.option.yAxis[1].min = Math.min.apply(null, dataZhu);
      this.echartsData.line = {
        xD: xData,
        yD: data,
        zD: dataZhu,
      };
      this.updateEchartsLine();
      //this.$store.commit("updateChartData", this.echartsData);
    },
    getLineChartsData() {
      //   this.changeOption(
      //     this.echartsData["line"].xD,
      //     this.echartsData["line"].yD,
      //     this.echartsData["line"].zD
      //   );

      this.$fetch("api/trend!execute.action", { symbol: this.itemName }).then(
        (res) => {
          var jsonArray = res;
          var xdata = jsonArray.data.map(function (item) {
            return item.current_time;
          });
          var data = jsonArray.data.map(function (item) {
            return item.trend;
          });
          var dataZhu = jsonArray.data.map(function (item) {
            return item.volume;
          });
          this.changeOption(xdata, data, dataZhu);
        }
      );
    },
    getChartsData(lineType) {
      this.$fetch("api/kline!execute.action", {
        symbol: this.itemName,
        line: lineType,
      }).then((res) => {
        var jsonArray = res;

        var xdataK = jsonArray.data.map(function (item) {
          return item.current_time;
        });
        // 可视区间段40条数据
        var len = xdataK.length;
        var bRate = 1;
        if (len > 100) {
          bRate = 100 / len;
        }
        var start = 100 - bRate * 100;
        // if (!this.optionsKline.start) {
        //   this.option.dataZoom[0].start = start;
        //   this.optionK.dataZoom[1].start = start;
        // }

        var valueK = jsonArray.data.map(function (item) {
          return item.close;
        });
        var volumeK = jsonArray.data.map(function (item, index) {
          return item.volume;
        });

        this.changeOption(xdataK, valueK, volumeK);
      });
    },
    getDetail() {
      this.$fetch("api/realtime!execute.action", {
        symbol: this.itemName,
      }).then((res) => {
        var jsonArray = res;
        this.moneyInfo = jsonArray.data[0];
      });
    },
  },
};
</script>
<style scoped lang="css">
@import url("@/assets/css/market/coinDetail.css");
</style>
