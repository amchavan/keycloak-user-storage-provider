<!--
    ***********************************************************
    * amchavan, 20-Oct-2020, from themes/base/login/login.ftl *
    ***********************************************************
-->

<#import "template.ftl" as layout>

<div class="container-fluid">

    <!-- Page header -->
    <div class="container-fluid alma-blue">
        <div class="row">
            <a class="tab-head" id="home-tab" href="https://asa.alma.cl/">
                <img class="alma-logo" src="${url.resourcesPath}/img/alma-logo.png">
                <!-- <strong>Atacama Large Millimeter/submillimeter Array</strong> -->
            </a>

            <span class="flex-md-fill"></span>
        </div>
    </div>

    <div style="padding-bottom: 5px"></div>

    <!-- Page body, two columns -->
    <div class="container-fluid">

        <div style="padding-bottom: 5px"></div>

        <div class="row">

            <!-- Left column, credentials entry -->
            <div class="col-sm-4">

                <@layout.registrationLayout displayInfo=social.displayInfo displayWide=(realm.password && social.providers??); section>
                    <#if section = "header">
                    <h3>
                        Enter your Userid and Password
                    </h3>

                    <#elseif section = "form">

                    <div id="kc-form">
                      <div id="kc-form-wrapper">
                        <#if realm.password>
                            <form id="kc-form-login" onsubmit="login.disabled = true; return true;" action="${url.loginAction}" method="post">
                                <div class="${properties.kcFormGroupClass!}">
                                    <label for="username" class="${properties.kcLabelClass!}">Userid</label>
                                    <input tabindex="1" id="username" class="${properties.kcInputClass!}" name="username" value="${(login.username!'')}"  type="text" autofocus autocomplete="off" />
                                </div>

                                <div class="${properties.kcFormGroupClass!}">
                                    <label for="password" class="${properties.kcLabelClass!}">${msg("password")}</label>
                                    <input tabindex="2" id="password" class="${properties.kcInputClass!}" name="password" type="password" autocomplete="off" />
                                </div>


                                <!-- KEEP THIS - In case we want to introduce the Remember Me feature
                                <div class="${properties.kcFormGroupClass!} ${properties.kcFormSettingClass!}">
                                    <div id="kc-form-options">
                                        <#if realm.rememberMe && !usernameEditDisabled??>
                                            <div class="checkbox">
                                                <label>
                                                    <#if login.rememberMe??>
                                                        <input tabindex="3" id="rememberMe" name="rememberMe" type="checkbox" checked> ${msg("rememberMe")}
                                                    <#else>
                                                        <input tabindex="3" id="rememberMe" name="rememberMe" type="checkbox"> ${msg("rememberMe")}
                                                    </#if>
                                                </label>
                                            </div>
                                        </#if>
                                    </div>
                                  </div>
                                  -->

                                  <div id="kc-form-buttons" class="${properties.kcFormGroupClass!}">
                                      <input type="hidden" id="id-hidden-input" name="credentialId"/>
                                      <input tabindex="4" class="${properties.kcButtonClass!} ${properties.kcButtonPrimaryClass!} ${properties.kcButtonBlockClass!} ${properties.kcButtonLargeClass!}" name="login" id="kc-login" type="submit" value="${msg("doLogIn")}"/>
                                  </div>
                            </form>
                        </#if>
                        </div>

                        <!-- KEEP THIS - In case we want to delegate login to CAS
                        <#if realm.password && social.providers??>
                            <div id="kc-social-providers" class="${properties.kcFormSocialAccountContentClass!} ${properties.kcFormSocialAccountClass!}">
                                <ul class="${properties.kcFormSocialAccountListClass!} <#if social.providers?size gt 4>${properties.kcFormSocialAccountDoubleListClass!}</#if>">
                                    <#list social.providers as p>
                                        <li class="${properties.kcFormSocialAccountListLinkClass!}"><a href="${p.loginUrl}" id="zocial-${p.alias}" class="zocial ${p.providerId}"> <span>${p.displayName}</span></a></li>
                                    </#list>
                                </ul>
                            </div>
                        </#if>
                        -->

                      </div>

                    </#if>

                </@layout.registrationLayout>

            </div>

            <!-- Separator -->
            <div class="col-sm-1"> &nbsp; </div>

            <!-- Right column, info & links -->
            <div class="col-sm-4">
                <h3> &nbsp; </h3>

                For security reasons, please Log Out and Exit your web browser
                when you are done accessing services that require authentication!
                <hr>

                If you don't have an account, you can create one in the following link:<br>
                <a href="http://asa.alma.cl/UserRegistration/newAccount.jsp">
                    Registration web form
                </a>
                <hr>

                <!-- The following two sections have the same link, unify? -->
                If you forgot you account ID, you can go to the following link:<br>
                <a href="http://asa.alma.cl/UserRegistration/forgotAccount.jsp">
                    Forgot account ID page
                </a>
                <hr>

                If you want to reset your password, you can go to the following link:<br>
                <a href="http://asa.alma.cl/UserRegistration/forgotPassword.jsp">
                    Reset password page
                </a>
                <hr>

                You may find a solution to your problem in the Support Center/Knowledgebase:<br>
                <a href="https://help.almascience.org/cas-login.php">
                    Helpdesk
                </a>


            </div>
        </div>

        <div style="padding-bottom: 30px"></div>

        <div class="row">
            <div class="col-sm-2"></div>

            <div class="col-sm-6">
                <hr>
                <span class="mini-faint-text">
                    Copyright © 2011-2021 Atacama Large Millimeter/submillimeter
                    Array (ALMA). All rights reserved.
                </span>
            </div>

            <div class="col-sm-4"></div>
        </div>
    </div>
</div>
