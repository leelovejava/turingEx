<template>
  <div class="f10-container">
    <!-- <div class="tabs flex">
      <div class="tab-item flex-1">
        {{ t('简况') }}
      </div>
    </div> -->
    <!-- A股 -->
    <div v-if="stocksType === 'A-stocks'">
      <p class="title">{{ t('latestIndicators') }}</p>
      <section class="value-container">
        <div class="flex-r">
          <ul class="flex-r-item">
            <li>
              <span class="label"> {{ t('PRatio') }} (TTM)</span>
              <span class="value">{{ details.peTtm }}</span>
            </li>
            <li>
              <span class="label">{{ t('earningsperShare') }}</span>
              <span class="value">{{ details.eps }}</span>
            </li>
            <li>
              <span class="label"> {{ t('TotalOperatingIncome') }} </span>
              <span class="value">{{ details.totalOperatingRevenue }}</span>
            </li>
            <li>
              <span class="label">{{ t('NetProfityuan') }}</span>
              <span class="value">{{ details.netProfit }}</span>
            </li>
            <li>
              <span class="label">{{ t('grosProfitMargin') }}</span>
              <span class="value">{{ details.grossProfitMargin }}</span>
            </li>
            <li>
              <span class="label">{{ t('Roe') }}</span>
              <span class="value">{{ details.roe }}</span>
            </li>
            <li>
              <span class="label">{{ t('totalShareCapital') }}</span>
              <span class="value">{{ details.commonAcs }}</span>
            </li>
            <li>
              <span class="label">{{ t('CirculatingAShares') }}</span>
              <span class="value">{{ details.circulatingAShares }}</span>
            </li>
            <li>
              <span class="label">{{ t('PledgeRatio') }}</span>
              <span class="value">{{ details.loanToValueRatio }}</span>
            </li>
          </ul>
          <ul class="flex-r-item">
            <li>
              <span class="label">{{ t('PriceToBookRatio') }} </span>
              <span class="value">{{ details.pbTtm }}</span>
            </li>
            <li>
              <span class="label">{{ t('NetAssetPerShareYua') }} </span>
              <span class="value">{{ details.bps }}</span>
            </li>
            <li>
              <span class="label">{{ t('TotOperatingYear') }}</span>
              <span class="value">{{ details.yoyTotalOperatingRevenue }}</span>
            </li>
            <li>
              <span class="label">{{ t('Netprofiyearonyear') }}</span>
              <span class="value">{{ details.yoyNetProfit }}</span>
            </li>
            <li>
              <span class="label">{{ t('neProfitMargin') }}</span>
              <span class="value">{{ details.netProfit }}</span>
            </li>
            <li>
              <span class="label">{{ t('debtRatio') }} </span>
              <span class="value">{{ details.debtRatio }}</span>
            </li>
            <li>
              <span class="label">{{ t('TotalMarketValueYuan') }}</span>
              <span class="value">{{ details.skMarketCap }}</span>
            </li>
            <li>
              <span class="label">{{ t('CirculationAMarketValueYuan') }}</span>
              <span class="value">{{ details.circulatingAShareMarketCapitalization }}</span>
            </li>
            <li>
              <span class="label">{{ t('Goodwillscale') }}</span>
              <span class="value">{{ details.goodwillScale }}</span>
            </li>
          </ul>
        </div>
      </section>
      <p class="title">{{ t('基本资料') }}</p>
      <section class="value-container base-info">
        <div class="flex-r">
          <ul class="flex-r-item">
            <li>
              <span class="label">{{ t('CompanyName') }}</span>
              <span class="value">{{ details.orgName }}</span>
            </li>
            <li>
              <span class="label">{{ t('AshareCode') }}</span>
              <span class="value">{{ details.strCodeA }}</span>
            </li>
            <li>
              <span class="label">{{ t('AbbreviationAshares') }}</span>
              <span class="value">{{ details.strNameA }}</span>
            </li>
            <li>
              <span class="label">{{ t('Region') }}</span>
              <span class="value">{{ details.region }}</span>
            </li>
            <li>
              <span class="label">{{ t('Industry') }}</span>
              <span class="value">{{ details.belongIndustry }}</span>
            </li>
            <li>
              <span class="label">{{ t('BelongingConcept') }}</span>
              <span class="value">{{ details.belongingConcept }}</span>
            </li>
            <li>
              <span class="label">{{ t('chairman') }}</span>
              <span class="value">{{ details.chairman }}</span>
            </li>
            <li>
              <span class="label">{{ t('legalRepresentative') }}</span>
              <span class="value">{{ details.legalRepresentative }}</span>
            </li>
            <li>
              <span class="label">{{ t('GeneralManager') }}</span>
              <span class="value">{{ details.managingDirector }}</span>
            </li>
            <li>
              <span class="label">{{ t('Secretary') }}</span>
              <span class="value">{{ details.secretary }}</span>
            </li>
            <li>
              <span class="label">{{ t('Dateofestablishment') }}</span>
              <span class="value">{{ details.foundDate }}</span>
            </li>
            <li>
              <span class="label">{{ t('RegisteredCapitalYuan') }}</span>
              <span class="value">{{ details.registeredCapital }}</span>
            </li>
            <li>
              <span class="label">{{ t('Numberofemployees') }}</span>
              <span class="value">{{ details.empNum }}</span>
            </li>
            <li>
              <span class="label">{{ t('Auditor') }}</span>
              <span class="value">{{ details.auditOrganization }}</span>
            </li>
            <li>
              <span class="label">{{ t('alegaladvisoraa') }}</span>
              <span class="value">{{ details.legalAdvisor }}</span>
            </li>
            <li>
              <span class="label">{{ t('联系电话') }}</span>
              <span class="value">{{ details.orgTel }}</span>
            </li>
            <li>
              <span class="label">{{ t('CompanyEmail') }}</span>
              <span class="value">{{ details.orgEmail }}</span>
            </li>
            <li>
              <span class="label">{{ t('companyWebsite') }}</span>
              <span class="value">{{ details.orgWeb }}</span>
            </li>
            <li>
              <span class="label">{{ t('officeAddress') }}</span>
              <span class="value">{{ details.address }}</span>
            </li>
            <li>
              <span class="label">{{ t('RegisteredAddress') }}</span>
              <span class="value">{{ details.regAddress }}</span>
            </li>
            <li>
              <span class="label">{{ t('CompanyProfile') }}</span>
              <span class="value">
                {{ details.orgProfile }}
              </span>
            </li>
          </ul>
        </div>
      </section>
    </div>

    <!-- 美股 -->
    <div v-if="stocksType === 'US-stocks'">
      <p class="title">{{ t('latestIndicators') }}</p>
      <section class="value-container">
        <div class="flex-r">
          <ul class="flex-r-item">
            <li>
              <span class="label">{{ t('PRatio') }}(TTM)</span>
              <span class="value">{{ details.peTtm }}</span>
            </li>
            <li>
              <span class="label">{{ t('earningsperShare') }}</span>
              <span class="value">{{ details.eps }}</span>
            </li>
            <li>
              <span class="label">{{ t('DividendpershareUSD') }}</span>
              <span class="value">{{ details.dpsHkd }}</span>
            </li>
            <li>
              <span class="label">{{ t('totalIncome') }}</span>
              <span class="value">{{ details.totalIncome }}</span>
            </li>
            <li>
              <span class="label">{{ t('NeinteresRateAttributableToparent') }}</span>
              <span class="value">{{ details.profitParentCompany }}</span>
            </li>
            <li>
              <span class="label">{{ t('grosProfitMargin') }} </span>
              <span class="value">{{ details.grossProfitMargin }}</span>
            </li>
            <li>
              <span class="label">{{ t('totalShareCapital') }}</span>
              <span class="value">{{ details.commonAcs }}</span>

            </li>
          </ul>
          <ul class="flex-r-item">
            <li>
              <span class="label">{{ t('PriceToBookRatio') }} </span>
              <span class="value">{{ details.pbTtm }}</span>
            </li>
            <li>
              <span class="label">{{ t('NetAssetPerShareYua') }} </span>
              <span class="value">{{ details.bps }}</span>
            </li>
            <li>
              <span class="label">{{ t('interestRate') }}</span>
              <span class="value">{{ details.weeklyInterestRate }}</span>
            </li>
            <li>
              <span class="label">{{ t('Totalrevenue') }}</span>
              <span class="value">{{ details.yoyTotalIncome }}</span>
            </li>
            <li>
              <span class="label">{{ t('Netattributable') }}</span>
              <span class="value">{{ details.profitParentCompany }}</span>
            </li>
            <li>
              <span class="label">{{ t('netProfit') }}</span>
              <span class="value">{{ details.netProfit }}</span>
            </li>
            <li>
              <span class="label">{{ t('neProfitMargin') }}</span>
              <span class="value">{{ details.netProfitRatio }}</span>
            </li>
          </ul>
        </div>
      </section>
      <p class="title">{{ t('SecuritiesInformation') }}</p>
      <section class="value-container">
        <div class="flex-r">
          <ul class="flex-r-item">
            <li>
              <span class="label">{{ t('SecuritiesCode') }}</span>
              <span class="value">{{ details.symbol }}</span>
            </li>
            <li>
              <span class="label">{{ t('securityType') }}</span>
              <span class="value">{{ details.securityType }}</span>
            </li>
            <li>
              <span class="label">{{ t('listingDate') }}</span>
              <span class="value">{{ details.listingDate }}</span>
            </li>
            <li>
              <span class="label">{{ t('USstocKMarketValue') }}</span>
              <span class="value">{{ details.skMarketCap }}</span>
            </li>
          </ul>
          <ul class="flex-r-item">
            <li>
              <span class="label">{{ t('AbbreviationAshares') }}</span>
              <span class="value">{{ details.strNameA }}</span>
            </li>
            <li>
              <span class="label">{{ t('listingPlace') }}</span>
              <span class="value">{{ details.stockExchange }}</span>
            </li>
            <li>
              <span class="label">{{ t('YearendDate') }}</span>
              <span class="value">{{ details.yearSettleDay }}</span>
            </li>
          </ul>
        </div>
      </section>
      <p class="title">{{ t('companyinformation') }}</p>
      <section class="value-container base-info">
        <div class="flex-r">
          <ul class="flex-r-item">
            <li>
              <span class="label">{{ t('CompanyName') }}</span>
              <span class="value">{{ details.orgName }}</span>
            </li>
            <li>
              <span class="label">{{ t('stockName') }}</span>
              <span class="value">{{ details.symbolName }}</span>
            </li>
            <li>
              <span class="label">{{ t('Industry') }}</span>
              <span class="value">{{ details.belongIndustry }}</span>
            </li>
            <li>
              <span class="label">{{ t('chairman') }}</span>
              <span class="value">{{ details.chairman }}</span>
            </li>
            <li>
              <span class="label">{{ t('Dateofestablishment') }}</span>
              <span class="value">{{ details.foundDate }}</span>
            </li>
            <li>
              <span class="label">{{ t('Numberofemployees') }}</span>
              <span class="value">{{ details.empNum }}</span>
            </li>
            <li>
              <span class="label">{{ t('regAddress') }}</span>
              <span class="value">{{ details.regAddress }}</span>
            </li>
            <li>
              <span class="label">{{ t('officeAddress') }}</span>
              <span class="value">{{ details.address }}</span>
            </li>
            <li>
              <span class="label">{{ t('companyWebsite') }}</span>
              <span class="value">{{ details.orgWeb }}</span>
            </li>
            <li>
              <span class="label">{{ t('CompanyEmail') }}</span>
              <span class="value">{{ details.orgEmail }}</span>
            </li>
            <li>
              <span class="label">{{ t('telephoneNumber') }}</span>
              <span class="value">{{ details.orgTel }}</span>
            </li>
            <li>
              <span class="label">{{ t('faxNumber') }}</span>
              <span class="value">{{ details.orgFax }}</span>
            </li>
            <li>
              <span class="label">{{ t('CompanyProfile') }}</span>
              <span class="value">
                {{ details.orgProfile }}
              </span>
            </li>
          </ul>
        </div>
      </section>
    </div>

    <!-- 港股 -->
    <div v-if="stocksType === 'HK-stocks'">
      <p class="title">{{ t('latestIndicators') }}</p>
      <section class="value-container">
        <div class="flex-r">
          <ul class="flex-r-item">
            <li>
              <span class="label">{{ t('PRatio') }}(TTM)</span>
              <span class="value">{{ details.peTtm }}</span>
            </li>
            <li>
              <span class="label">{{ t('earningsperShare') }}</span>
              <span class="value">{{ details.eps }}</span>
            </li>
            <li>
              <span class="label">{{ t('DividendperShare') }}</span>
              <span class="value">{{ details.dpsHkd }}</span>
            </li>
            <li>
              <span class="label">{{ t('grosProfitMargin') }}</span>
              <span class="value">{{ details.grossProfitMargin }}</span>
            </li>
            <li>
              <span class="label">{{ t('turnover') }}</span>
              <span class="value">{{ details.operateIncome }}</span>
            </li>
            <li>
              <span class="label">{{ t('ProfitShareholders') }}</span>
              <span class="value">{{ details.shhareholdersEarnings }}</span>
            </li>
            <li>
              <span class="label">{{ t('HonKongShareCapital') }}</span>
              <span class="value">{{ details.hongKongStockCapital }}</span>
            </li>
            <li>
              <span class="label">{{ t('totalShareCapital') }}</span>
              <span class="value">{{ details.commonAcs }}</span>
            </li>
          </ul>
          <ul class="flex-r-item">
            <li>
              <span class="label">{{ t('PriceToBookRatio') }}</span>
              <span class="value">{{ details.pbTtm }}</span>
            </li>
            <li>
              <span class="label">{{ t('NetAssetPerShareYua') }} </span>
              <span class="value">{{ details.bps }}</span>
            </li>
            <li>
              <span class="label">{{ t('dividendYeld') }}</span>
              <span class="value">{{ details.dividendRate }}</span>
            </li>
            <li>
              <span class="label">{{ t('neProfitMargin') }}</span>
              <span class="value">{{ details.netProfitRatio }}</span>
            </li>
            <li>
              <span class="label">{{ t('TurnoverYearyear') }}</span>
              <span class="value">{{ details.operateIncomeQoq }}</span>
            </li>
            <li>
              <span class="label">{{ t('ProfitYoY') }}</span>
              <span class="value">{{ details.yoyProfitShare }}</span>
            </li>
            <li>
              <span class="label">{{ t('HongKongstockMarket') }}</span>
              <span class="value">{{ details.hkskMarketCap }}</span>
            </li>
            <li>
              <span class="label">{{ t('TotalMarketCapitalization') }}</span>
              <span class="value">{{ details.skMarketCap }}</span>
            </li>
          </ul>
        </div>
      </section>
      <p class="title">{{ t('SecuritiesInformation') }}</p>
      <section class="value-container">
        <div class="flex-r">
          <ul class="flex-r-item">
            <li>
              <span class="label">{{ t('SecuritiesCode') }}</span>
              <span class="value">{{ details.symbol }}</span>
            </li>
            <li>
              <span class="label">{{ t('securityType') }}</span>
              <span class="value">{{ details.securityType }}</span>
            </li>
            <li>
              <span class="label">{{ t('listingDate') }}</span>
              <span class="value">{{ details.listingDate }}</span>
            </li>
            <li>
              <span class="label">{{ t('IssuePrice') }}</span>
              <span class="value">{{ details.issuePrice }}</span>
            </li>
            <li>
              <span class="label">{{ t('Sharesperlot') }}</span>
              <span class="value">{{ details.tradeUnit }}</span>
            </li>
          </ul>
          <ul class="flex-r-item">
            <li>
              <span class="label">ISIN</span>
              <span class="value">{{ details.isinCode }}</span>
            </li>
            <li>
              <span class="label">{{ t('plate') }}</span>
              <span class="value">{{ details.board }}</span>
            </li>
            <li>
              <span class="label">{{ t('YearendDate') }}</span>
              <span class="value">{{ details.yearSettleDay }}</span>
            </li>
            <li>
              <span class="label">{{ t('Issuanceshares') }}</span>
              <span class="value">{{ details.issuedCommonShares }}</span>
            </li>
            <li>
              <span class="label">{{ t('parValuepershare') }}</span>
              <span class="value">{{ details.parValuePerShare }}</span>
            </li>
          </ul>
        </div>
      </section>
      <p class="title">{{ t('companyinformation') }}</p>
      <section class="value-container base-info">
        <div class="flex-r">
          <ul class="flex-r-item">
            <li>
              <span class="label">{{ t('CompanyName') }}</span>
              <span class="value">{{ details.orgName }}</span>
            </li>
            <li>
              <span class="label">{{ t('EnglishName') }}</span>
              <span class="value">{{ details.enName }}</span>
            </li>
            <li>
              <span class="label">{{ t('Industry') }}</span>
              <span class="value">{{ details.belongIndustry }}</span>
            </li>
            <li>
              <span class="label">{{ t('HonKongShares') }}</span>
              <span class="value">{{ details.hongKongStockShares }}</span>
            </li>
            <li>
              <span class="label">{{ t('registeredCapital') }}</span>
              <span class="value">{{ details.registeredCapital }}</span>
            </li>
            <li>
              <span class="label">{{ t('chairman') }}</span>
              <span class="value">{{ details.chairman }}</span>
            </li>
            <li>
              <span class="label">{{ t('CompanySecretary') }}</span>
              <span class="value">{{ details.secretary }}</span>
            </li>
            <li>
              <span class="label">{{ t('Dateofestablishment') }}</span>
              <span class="value">{{ details.foundDate }}</span>
            </li>
            <li>
              <span class="label">{{ t('Numberofemployees') }}</span>
              <span class="value">{{ details.empNum }}</span>
            </li>
            <li>
              <span class="label">{{ t('Business') }}</span>
              <span class="value">
                {{ details.orgProfile }}
              </span>
            </li>
            <li>
              <span class="label">{{ t('registeredOffice') }}</span>
              <span class="value">{{ details.regAddress }}</span>
            </li>
          </ul>
        </div>
      </section>
    </div>

    <!-- 台股 -->
    <div v-if="stocksType === 'TW-stocks'">
      <p class="title">{{ t('latestIndicators') }}</p>
      <section class="value-container target">
        <div class="flex">
          <ul class="flex">
            <li>
              <span class="label">{{ t('Open')}}</span>
              <span class="value">{{chartData.open}}</span>
            </li>
            <li>
              <span class="label">{{ t('最高')}}</span>
              <span class="value">{{ chartData.high}}</span>
            </li>
            <li>
              <span class="label">{{ t('成交量')}}</span>
              <span class="value">{{ formatMoney(chartData?.volume)}}</span>
            </li>
            <li>
              <span class="label">{{ t('Close')}}</span>
              <span class="value">{{chartData.open}}</span>
            </li>
            <li>
              <span class="label">{{ t('最低')}}</span>
              <span class="value">{{ chartData.low}}</span>
            </li>
            <li>
              <span class="label">{{ t('成交额')}}</span>
              <span class="value">{{ formatMoney(chartData?.tamount)}}</span>
            </li>
            <li>
              <span class="label">{{ t('均价')}}</span>
              <span class="value">{{ chartData.averagePrice}}</span>
            </li>
            <li>
              <span class="label">{{ t('本益比')}}</span>
              <span class="value">{{ chartData.earningsRatio}}</span>
            </li>
            <li>
              <span class="label">{{ t('marketValue')}}</span>
              <span class="value">{{ formatMoney(chartData.marketCapital)}}</span>
            </li>
          </ul>
        </div>
        
      </section>
      <div class="divider"></div>
      <section class="value-container target">
        <div class="flex">
          <ul class="flex">
            <li>
              <span class="label">{{ t('amplitude')}}</span>
              <span class="value">{{ chartData.eps}}</span>
            </li>
            <li>
              <span class="label">{{ t('周转率')}}</span>
              <span class="value">{{ chartData.turnoverRate}}%</span>
            </li>
            <li>
              <span class="label">{{ t('发行股')}}</span>
              <span class="value">{{ formatMoney(chartData.issuedShares)}}</span>
            </li>
            <li>
              <span class="label">{{ t('涨停')}}</span>
              <span class="value">{{ chartData.dailyLimit}}</span>
            </li>
            <li>
              <span class="label">{{ t('52W高')}}</span>
              <span class="value">{{ chartData.w52High}}</span>
            </li>
            <li>
              <span class="label">{{ t('内盘量')}}</span>
              <span class="value">{{ chartData.internalVolume}}</span>
            </li>
            <li>
              <span class="label">{{ t('跌停')}}</span>
              <span class="value">{{ chartData.limitDown}}</span>
            </li>
            <li>
              <span class="label">{{ t('52W低')}}</span>
              <span class="value">{{ chartData.w52Low}}</span>
            </li>
            <li>
              <span class="label">{{ t('外盘量')}}</span>
              <span class="value">{{ chartData.externalVolume}}</span>
            </li>
          </ul>
        </div>
        
      </section>
      <div class="divider"></div>
      <section class="value-container target">
        <div class="flex">
          <ul class="flex">
            <li>
              <span class="label">{{ t('近四季度EPS')}}</span>
              <span class="value">{{ chartData.epsYear }}</span>
            </li>
            <li>
              <span class="label">{{ t('当季EPS')}}</span>
              <span class="value">{{ chartData.epsQuarter}}</span>
            </li>
            <li>
              <span class="label">{{ t('grosProfitMargin')}}</span>
              <span class="value">{{  chartData.grossProfitMargin}}%</span>
            </li>
            <li>
              <span class="label">{{ t('每股净值')}}</span>
              <span class="value">{{  chartData.netValuePerShare}}</span>
            </li>
            <li>
              <span class="label">{{ t('本净比')}}</span>
              <span class="value">{{ chartData.priceToNetRatio}}</span>
            </li>
            <li>
              <span class="label">{{ t('营益率')}}</span>
              <span class="value">{{ chartData.profitMargin}}%</span>
            </li>
            <li>
              <span class="label">{{ t('年股利')}}</span>
              <span class="value">{{ chartData.annualDividend}}</span>
            </li>
            <li>
              <span class="label">{{ t('殖利率')}}</span>
              <span class="value">{{  chartData.yield}}%</span>
            </li>
            <li>
              <span class="label">{{ t('净利率')}}</span>
              <span class="value">{{ chartData.netProfitMargin}}%</span>
            </li>
          </ul>
        </div>
        
      </section>
      <p class="title">{{ t('companyinformation') }}</p>
      <section class="value-container base-info">
        <div class="flex-r">
          <ul class="flex-r-item">
            <li>
              <span class="label">{{ t('CompanyName') }}</span>
              <span class="value">{{ details.orgName }}</span>
            </li>
            <li>
              <span class="label">{{ t('台股代码') }}</span>
              <span class="value">{{ details.strCodeA }}</span>
            </li>
            <li>
              <span class="label">{{ t('台股简称') }}</span>
              <span class="value">{{ details.strNameA }}</span>
            </li>
            <li>
              <span class="label">{{ t('所属地区') }}</span>
              <span class="value">{{ details.region }}</span>
            </li>
            <li>
              <span class="label">{{ t('chairman') }}</span>
              <span class="value">{{ details.chairman }}</span>
            </li>
            <li>
              <span class="label">{{ t('GeneralManager') }}</span>
              <span class="value">{{ details.managingDirector }}</span>
            </li>
            <li>
              <span class="label">{{ t('发言人') }}</span>
              <span class="value">{{ details.secretary }}</span>
            </li>
            <li>
              <span class="label">{{ t('Dateofestablishment') }}</span>
              <span class="value">{{ details.foundDate }}</span>
            </li>
            <li>
              <span class="label">{{ t('listingDate') }}</span>
              <span class="value">{{ details.listingDate }}</span>
            </li>
            <li>
              <span class="label">{{ t('上柜日期') }}</span>
              <span class="value">{{ details.yearSettleDay }} </span>
            </li>
          </ul>
        </div>
      </section>
      <p class="title">{{ t('基本资料') }}</p>
      <section class="value-container base-info">
        <div class="flex-r">
          <ul class="flex-r-item">
            <li>
              <span class="label">{{ t('Industry') }}</span>
              <span class="value">{{ details.belongIndustry }}</span>
            </li>
            <li>
              <span class="label">{{ t('过户机构') }}</span>
              <span class="value">{{ details.securityType }}</span>
            </li>
            <li>
              <span class="label">{{ t('公司地址') }}</span>
              <span class="value">{{details.address }}</span>
            </li>
            <li>
              <span class="label">{{ t('过户地址') }}</span>
              <span class="value">{{ details.regAddress }}</span>
            </li>
            <li>
              <span class="label">{{ t('公司电话') }}</span>
              <span class="value">{{ details.orgTel }}</span>
            </li>
            <li>
              <span class="label">{{ t('过户电话') }}</span>
              <span class="value">{{ details.orgFax }}</span>
            </li>
            <li>
              <span class="label">{{ t('电子邮件') }}</span>
              <span class="value">{{ details.orgEmail }}</span>
            </li>
            <li>
              <span class="label">{{ t('主要经营业务') }}</span>
              <span class="value">{{ details.businessScope }}</span>
            </li>
          </ul>
        </div>
      </section>
      <p class="title">{{ t('服务相关') }}</p>
      <section class="value-container base-info">
        <div class="flex-r">
          <ul class="flex-r-item">
            <li>
              <span class="label">{{ t('实收资本额') }}</span>
              <span class="value">{{ details.skMarketCap }}</span>
            </li>
            <li>
              <span class="label">{{ t('人均持股') }}</span>
              <span class="value">{{ details.eps }}</span>
            </li>
            <li>
              <span class="label">{{ t('已发行股数') }}</span>
              <span class="value">{{details.commonAcs }}</span>
            </li>
            <li>
              <span class="label">{{ t('外资持股') }}</span>
              <span class="value">{{ details.dpsHkd }}</span>
            </li>
            <li>
              <span class="label">{{ t('股东人数') }}</span>
              <span class="value">{{ details.empNum }}</span>
            </li>
            <li>
              <span class="label">{{ t('前十大持股') }}</span>
              <span class="value">{{ details.pbTtm }}</span>
            </li>
            <li>
              <span class="label">{{ t('特别股') }}</span>
              <span class="value">{{ details.grossProfitMargin }}</span>
            </li>
            <li>
              <span class="label">{{ t('公司发行') }}</span>
              <span class="value">{{ details.profitParentCompany }}</span>
            </li>
          </ul>
        </div>
      </section>
    </div>

  </div>
</template>
      
<script setup>
import { ref, watch, reactive } from 'vue';
import { useI18n } from 'vue-i18n'
import { _getItemSummary } from '@/service/etf.api'
import { useRoute, useRouter } from 'vue-router';
import { formatMoney } from "@/utils";
const route = useRoute()
const { t } = useI18n()
const stocksType = route.query.symbolType || 'US-stocks'
const props = defineProps({
  details: {
    type: Object,
    default: () => {
      return {}
    }
  },
  chartData: {
    type: Object,
    default: () => {
      return {}
    }
  }
})
let details = reactive(props.details)
let chartData = reactive(props.chartData)
const twTargetList = reactive([
  { name: t('今开'), value: chartData.low },
  { name: t('最高'), value: '96.00' },
  { name: t('成交量'), value: '96.00' },
  { name: t('昨收'), value: '96.00' },
  { name: t('最低'), value: '96.00' },
  { name: t('成交额'), value: '96.00' },
  { name: t('均价'), value: '96.00' },
  { name: t('本益比'), value: '96.00' },
  { name: t('市值'), value: '96.00' },
])
watch(() => props.details, (nv) => {
  Object.assign(details, nv)
})
watch(() => props.chartData, (nv) => {
  Object.assign(chartData, nv)
})

</script>
<style lang="scss" scoped>
:deep(.van-sidebar) {
  width: 100%;
}

:deep(.van-sidebar-item) {
  background-color:  $main2_background;
  color: $text_color;
  padding: 10px;
}

:deep(.van-sidebar-item--select) {
  background-color: $select-bg;
  color: $color_main;
}

.container-box {
  padding: 0 10px 50px 10px;

  .green {
    color: $green;
  }

  .red {
    color: $red;
  }

  .header {
    display: flex;
    align-items: center;

    .flex-l {
      flex: 1;
      display: inline-flex;
      align-items: center;

      .icon {
        margin-right: 10px;
        display: inline-block;
        width: 20px;
        height: 20px;

        img {
          height: 20px;
          width: 20px;
        }
      }

      .name-box {
        .title {
          font-size: 14px;
          font-weight: 700;
          line-height: 16px;
        }

        .type {
          font-size: 12px;
          color: $text_color6;
        }
      }
    }

    .icon-group {
      width: 100px;
      text-align: right;

      .icon {
        display: inline-block;
        width: 28px;
        height: 28px;
        padding: 4px;
        margin-left: 16px;
      }
    }


  }

  .value-container {
    display: flex;
    justify-content: space-between;
    font-size: 12px;
    color: #747A8F;
    border-bottom: 1px solid $border_color;

    .flex-l {
      display: flex;
      flex-direction: column;
      justify-content: center;
      width: 120px;

      .first-line {
        font-weight: 700;
        font-size: 32px;
      }

      .second-line {
        margin-top: 8px;
      }
    }

    .flex-r {
      flex: 1;
      display: flex;
      align-items: center;
      color: $text_color;

      .flex-r-item {
        flex: 1;
        text-align: center;

        .label {
          color: $f10-text;;
          margin-right: 10px;
          text-align: left;
          padding-left: 10px;
        }
      }
    }
  }

  .base-info {
    .flex-r-item {
      margin: 0 !important;

      .label {
        display: inline-block;
        width: 100px;
        text-align: left;
      }

      .value {
        text-align: left;
        flex: 1;
      }
    }
  }

  .indicator-index-container {
    .indicator-index-box {
      padding: 12px 0;
      display: flex;
      justify-content: space-between;
      align-items: center;

      .flex-l {
        flex: 1;

        ul {
          display: flex;

          li {
            flex: 1;
            text-align: center;
            margin: 0 4px;
            font-size: 12px;
            border-radius: 4px;
          }
        }
      }

      .flex-r {
        display: flex;
        justify-content: flex-end;
        width: 80px;

        img {
          width: 12px;
          height: 16px;
        }

      }
    }

    .active {
      background: $btn_main;
      color: $white;
    }

    .indicator-index-box-second {
      ul {
        display: flex;
        border: 1px solid $border_color;
        align-items: center;
        border-right: none;
      }

      li {
        flex: 1;
        height: 32px;
        line-height: 32px;
        text-align: center;
        font-size: 12px;
        border-right: 1px solid $border_color;
      }
    }
  }

  .kline-container {
    margin-top: 10px;

    .order-book-container {
      padding: 100px 2px 0 6px;
      width: 130px;
      border-left: 1px solid #2E364F;
    }

    .chart-index {
      flex: 1;
      min-width: 200px;
    }

 

    .text-sm {
      position: relative;
    }

    .select-div {
      width: 100px;
      position: absolute;
      top: 30px;
      left: 0;
      z-index: 100;

      ul {
        box-shadow: 0px 3px 11px 0px rgb(0 0 0 / 10%);

        li {
          background: $mainbgWhiteColor;
          text-align: center;
          padding: 10px 0;
          font-size: 16px;
        }

        li:not(:last-child) {
          border-bottom: 1px solid $border-grey;
        }
      }
    }

    .active {
      background: $btn_main !important;
      color: $text_color;
    }
  }

  .all-etf-ranking {
    margin-top: 10px;

    .title {
      font-weight: 700;
      padding: 0 12px;
    }

    .tabs {
      padding: 0 12px;
      margin-top: 10px;
      // height: 40px;
      min-height: 40px;
      line-height: 24px;
      color: #BBBCBD;
      width: 280px;

      .tab-item {
        margin: 4px;
        text-align: center;
        padding: 4px 6px;
        font-size: 12px;
        color: $text_color5;
        background: $US_tab_background;
        border-radius: 10px;
        background-size: cover;
      }

      .active {
        font-weight: 700;
        color: $color_main !important;
        background: $US_tabActice_background;
        border-radius: 10px;
        background-size: cover;
      }
    }

    .etf-table {
      .right {
        text-align: right;
      }

      ul {
        margin-top: 10px;
      }

      .title-line {
        color: #747A8F;
        font-size: 12px;
        font-weight: 400;
        padding: 0 12px;
        border: none;
      }

      li {
        padding: 14px 12px;
        display: flex;
        font-size: 12px;
        line-height: 18px;
        border-bottom: 1px solid $border_color;

        .gray-text {
          color: #BCBDC2;
          font-size: 12px;
        }

        .flex-l {
          width: 100px;
        }

        .flex-r {
          display: inline-flex;
          flex: 1;

          .flex-r-item {
            flex: 1;
            align-self: center;
            text-align: center;
          }
        }
      }
    }
  }

  .f10-container {
    .title {
      font-size: 16px;
      margin: 16px 0 0;
      padding-bottom: 10px;
      border-bottom: 1px solid $border_color;
    }

    .tabs {
      height: 40px;
      line-height: 24px;
      color: #BBBCBD;
      width: 120px;

      .tab-item {
        margin: 4px;
        text-align: center;
        padding: 4px 6px;
        font-size: 12px;
        font-weight: 700;
        color: $color_main !important;
        background: $US_tabActice_background;
        border-radius: 10px;
        background-size: cover;
      }
    }

    .value-container {
      border: none;
    }

    .flex-r {
      align-items: flex-start;
    }

    .flex-r-item {
      li {
        display: flex;
        line-height: 28px;
      }
    }

    .flex-r-item:first-child {
      margin-right: 30px;
    }

  }

}

.target{
  padding: 0 3%;
  ul{
    gap: 0 3.3%;
    flex-wrap: wrap;
    border-bottom: 1px solid $border-color;
    padding: 2% 0;

    &:last-child{
      border: none;
    }

    li{
      width: 31%;
      flex-shrink: 0;

      display: flex;
      justify-content: space-between;
      align-items: center;

      .label{
        color: $f10-text;
      }
      .value{
        color: $text-color;
      }
    }
  }
}

.base-info{
  margin-top: 10px;
}
.divider{
  height: 1px !important;
}
</style>