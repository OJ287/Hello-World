/*
   システム名:AP総合管理システム
   機能名:受注登録

 */
/*
   Date       Author         Changes Key                                 Version
   -----------------------------------------------------------------------------
   2007-04-25 Fsol           Auto-Create                                 0.0.0
   2007-04-25 Isac)Katayama  Add                                         1.0.0
   2007-06-29 Fsol)Naruto    Update                                      1.0.1
 */
/**
* @fileOverview INTARFRM JavaScript Form
* @name intarfrm-form-c0801w02.js
* @author INTARFRM
*/
var confirmmsg_obj;
var acptkbnsysinfo_obj;
var price_obj;
var hyujyun_obj;
var acpt_obj;
(function (window) {
    'use strict';

    var intarfrm = window.fujitsu.intarfrm,
        helpers = intarfrm.helpers,
        actions, events, setFormEventHandler,
        programId = 'c0801w00', formId = 'c0801w02',
        formInfo = intarfrm.forms[formId],
        formUtil = formInfo.formUtil = new intarfrm.FormUtil(programId, formId),
        skeleton = formInfo.skeleton = new intarfrm.Skeleton(programId, formId);

    formInfo.actions = actions = {

        /**
        * フォームアクション[ボタン複写_他フォーム]の処理を実行します。
        * @param context フォームアクションコンテキスト
        */
        invokeBtnfukusha_frm: function (context) {
            /* このアクションの実行前の拡張処理はここに記述します。*/

			//ADD START 20190327 TJS)takio OM-00381
        	if (f$('c0801w02-acptdd') != null) {
    	        if (f$('c0801w02-acptdd').value == "") {
    	        	var msg = "「受注日」は必ず入力して下さい。";
    	            var buttonsOption = {
    	                'OK': function () {
    	                    $(this).dialog('close');
    	                    f$('c0801w02-acptdd').focus();
    	                }
    	            }
    	            $("#confirm-dialog").html(msg);
    	            $("#confirm-dialog").dialog({
    	                modal: true,
    	                buttons: buttonsOption
    	            });
    	            return false;
    	        }
    	        //10文字でない（日付型に変換できてない）場合
    	        if(f$('c0801w02-acptdd').value.length != 10){
    	        	var msg = "「受注日」は日付として認識できません。";
    	            var buttonsOption = {
    	                'OK': function () {
    	                    $(this).dialog('close');
    	                    f$('c0801w02-acptdd').focus();
    	                }
    	            }
    	            $("#confirm-dialog").html(msg);
    	            $("#confirm-dialog").dialog({
    	                modal: true,
    	                buttons: buttonsOption
    	            });
    	            return false;
    	        }
        	}
        	if (f$('c0801w02-dlvryytidd') != null) {
    	        if (f$('c0801w02-dlvryytidd').value == "") {
    	        	var msg = "「配達予定日」は必ず入力して下さい。";
    	            var buttonsOption = {
    	                'OK': function () {
    	                    $(this).dialog('close');
    	                    f$('c0801w02-dlvryytidd').focus();
    	                }
    	            }
    	            $("#confirm-dialog").html(msg);
    	            $("#confirm-dialog").dialog({
    	                modal: true,
    	                buttons: buttonsOption
    	            });
    	            return false;
    	        }
    	        //10文字でない（日付型に変換できてない）場合
	            /* 2019-12-18 Pkut)Yang OM-00412-000 update begin */
    	        //配達予定日スラッシュ追加編集
	        	dlvryytiddFormat();
	            /* 2019-12-18 Pkut)Yang OM-00412-000 update end   */
	        	if(f$('c0801w02-dlvryytidd').value.length != 10){
    	        	var msg = "「配達予定日」は日付として認識できません。";
    	            var buttonsOption = {
    	                'OK': function () {
    	                    $(this).dialog('close');
    	                    f$('c0801w02-dlvryytidd').focus();
    	                }
    	            }
    	            $("#confirm-dialog").html(msg);
    	            $("#confirm-dialog").dialog({
    	                modal: true,
    	                buttons: buttonsOption
    	            });
    	            return false;
    	        }
        	}
	        //ADD E N D 20190327 TJS)takio

            // フォームアクション実行
            skeleton.actions.invokeServerAction(context, function (context, data, xhr) {
                var fwData = data.__frameworkData;
                /* アクションの実行結果に対する拡張処理はここに記述します。*/

                skeleton.actions.formActionCommonCallBack(context, data, xhr);

            });
        },
        /**
        * フォームアクション[ボタン行複写_他フォーム]の処理を実行します。
        * @param context フォームアクションコンテキスト
        */
        invokeBtngyofukusha_frm: function (context) {
            /* このアクションの実行前の拡張処理はここに記述します。*/

            // フォームアクション実行
            skeleton.actions.invokeServerAction(context, function (context, data, xhr) {
                var fwData = data.__frameworkData;
                /* アクションの実行結果に対する拡張処理はここに記述します。*/

                skeleton.actions.formActionCommonCallBack(context, data, xhr);

            });
        },
        /**
        * フォームアクション[ボタン行挿入_明細行挿入]の処理を実行します。
        * @param context フォームアクションコンテキスト
        */
        invokeBtngyosonyu_mins: function (context) {
            /* このアクションの実行前の拡張処理はここに記述します。*/
        	/* このアクションの実行前の拡張処理はここに記述します。*/
        	context.postArgs["shncdadd"]=$("#c0801w02-shncdadd").val();

            // フォームアクション実行
            skeleton.actions.invokeServerAction(context, function (context, data, xhr) {
                var fwData = data.__frameworkData;
                /* アクションの実行結果に対する拡張処理はここに記述します。*/

                skeleton.actions.formActionCommonCallBack(context, data, xhr);

            });
        },
        /**
        * フォームアクション[ボタン行追加_明細行追加]の処理を実行します。
        * @param context フォームアクションコンテキスト
        */
        invokeBtngyotsuika_madd: function (context) {
            /* このアクションの実行前の拡張処理はここに記述します。*/

        	/* このアクションの実行前の拡張処理はここに記述します。*/
        	context.postArgs["shncdadd"]=$("#c0801w02-shncdadd").val();
            // フォームアクション実行
            skeleton.actions.invokeServerAction(context, function (context, data, xhr) {
                var fwData = data.__frameworkData;
                /* アクションの実行結果に対する拡張処理はここに記述します。*/

                skeleton.actions.formActionCommonCallBack(context, data, xhr);

            });
        },
        /**
        * フォームアクション[ボタン更新_他フォーム]の処理を実行します。
        * @param context フォームアクションコンテキスト
        */
        invokeBtnkshn_frm: function (context) {
        	/* このアクションの実行前の拡張処理はここに記述します。*/
        	/* 2017-12-11 IBIS)inoue add begin */
        	if ($(formUtil.getElementsByFormItemId('kyotensts')).val() != '1') {
        		f$('c0801w02-dlvrykyotencd').disabled = false;
        		f$('c0801w02-dlvryareacd2').disabled = false;
        	}
        	/* 2017-12-11 IBIS)inoue add end */

        	//処理区分によって確認メッセージを出し分ける
        	var msg = "";
        	if (f$('c0801w02-kshnkbn').value != "1") {
        		if (f$('c0801w02-prcskbn')[2].checked) {
        			if (f$('c0801w02-dnpysts').value == "11") {
        				msg = formUtil.getMessageResource("W080", "html", ["配達予定データ", "キャンセル"]);
        			} else {
        				msg = formUtil.getMessageResource("W054", "html", ["受注伝票"]);
        			}
        		} else if (f$('c0801w02-prcskbn')[3].checked) {
        			if (f$('c0801w02-dnpysts').value == "11") {
        				msg = formUtil.getMessageResource("W080", "html", ["配達予定データ", "削除"]);
        			} else {
        				msg = formUtil.getMessageResource("W055", "html", ["受注伝票"]);
        			}
        		} else {
        			if (f$('c0801w02-dnpysts').value == "11") {
        				msg = formUtil.getMessageResource("W080", "html", ["配達予定データ", "更新"]);
        			} else {
        				msg = formUtil.getMessageResource("I001", "html");
        			}
        		}
        	}
        	if (msg != "") {
        		// フォームアクション実行
                skeleton.actions.invokeServerActionWithConfirm(null, msg, context, function (context, data, xhr) {
                    var fwData = data.__frameworkData;
                    /* アクションの実行結果に対する拡張処理はここに記述します。*/

                    skeleton.actions.formActionCommonCallBack(context, data, xhr);
                });
        	}
            else {
                // フォームアクション実行
                skeleton.actions.invokeServerAction(context, function (context, data, xhr) {
                    var fwData = data.__frameworkData;
                    /* アクションの実行結果に対する拡張処理はここに記述します。*/

                    skeleton.actions.formActionCommonCallBack(context, data, xhr);

                });
            }
        },
        /**
        * フォームアクション[ボタン明細表示_他フォーム]の処理を実行します。
        * @param context フォームアクションコンテキスト
        */
        invokeBtnmeisaidsply_frm: function (context) {
            /* このアクションの実行前の拡張処理はここに記述します。*/

			//ADD START 20190327 TJS)takio OM-00381
        	if (f$('c0801w02-acptdd') != null) {
    	        if (f$('c0801w02-acptdd').value == "") {
    	        	var msg = "「受注日」は必ず入力して下さい。";
    	            var buttonsOption = {
    	                'OK': function () {
    	                    $(this).dialog('close');
    	                    f$('c0801w02-acptdd').focus();
    	                }
    	            }
    	            $("#confirm-dialog").html(msg);
    	            $("#confirm-dialog").dialog({
    	                modal: true,
    	                buttons: buttonsOption
    	            });
    	            return false;
    	        }
    	        //10文字でない（日付型に変換できてない）場合
    	        if(f$('c0801w02-acptdd').value.length != 10){
    	        	var msg = "「受注日」は日付として認識できません。";
    	            var buttonsOption = {
    	                'OK': function () {
    	                    $(this).dialog('close');
    	                    f$('c0801w02-acptdd').focus();
    	                }
    	            }
    	            $("#confirm-dialog").html(msg);
    	            $("#confirm-dialog").dialog({
    	                modal: true,
    	                buttons: buttonsOption
    	            });
    	            return false;
    	        }
        	}
        	if (f$('c0801w02-dlvryytidd') != null) {
    	        if (f$('c0801w02-dlvryytidd').value == "") {
    	        	var msg = "「配達予定日」は必ず入力して下さい。";
    	            var buttonsOption = {
    	                'OK': function () {
    	                    $(this).dialog('close');
    	                    f$('c0801w02-dlvryytidd').focus();
    	                }
    	            }
    	            $("#confirm-dialog").html(msg);
    	            $("#confirm-dialog").dialog({
    	                modal: true,
    	                buttons: buttonsOption
    	            });
    	            return false;
    	        }
    	        //10文字でない（日付型に変換できてない）場合
	            /* 2019-12-18 Pkut)Yang OM-00412-000 update begin */
    	        //配達予定日スラッシュ追加編集
	        	dlvryytiddFormat();
	            /* 2019-12-18 Pkut)Yang OM-00412-000 update end   */
	        	if(f$('c0801w02-dlvryytidd').value.length != 10){
    	        	var msg = "「配達予定日」は日付として認識できません。";
    	            var buttonsOption = {
    	                'OK': function () {
    	                    $(this).dialog('close');
    	                    f$('c0801w02-dlvryytidd').focus();
    	                }
    	            }
    	            $("#confirm-dialog").html(msg);
    	            $("#confirm-dialog").dialog({
    	                modal: true,
    	                buttons: buttonsOption
    	            });
    	            return false;
    	        }
        	}
	        //ADD E N D 20190327 TJS)takio

            // フォームアクション実行
            skeleton.actions.invokeServerAction(context, function (context, data, xhr) {
                var fwData = data.__frameworkData;
                /* アクションの実行結果に対する拡張処理はここに記述します。*/

                skeleton.actions.formActionCommonCallBack(context, data, xhr);

            });
        },
        /**
        * フォームアクション[ボタン戻る_他フォーム]の処理を実行します。
        * @param context フォームアクションコンテキスト
        */
        invokeBtnmodoru_frm: function (context) {
            /* このアクションの実行前の拡張処理はここに記述します。*/
            if (f$('c0801w02-syorimode').value != "6") {
                if (f$('c0801w02-prevscreen').value == 3) {
                    var openProcessFlg = openProcessSubWindow('s0302w00', getBackHomeParam());
                    if (openProcessFlg != false){
                        quitPage();
                    }
                    return false;
                } else if (f$('c0801w02-prevscreen').value == 4) {
                    var openProcessFlg = openProcessSubWindow('s0402w00', getBackHomeParam() + "&PREVSCREEN=5");
                    if (openProcessFlg != false){
                        quitPage();
                    }
                    return false;
                }
            }

            // フォームアクション実行
            skeleton.actions.invokeServerAction(context, function (context, data, xhr) {
                var fwData = data.__frameworkData;
                /* アクションの実行結果に対する拡張処理はここに記述します。*/

                skeleton.actions.formActionCommonCallBack(context, data, xhr);

            });
        },
        /**
        * フォームアクション[ボタンお気に入り_他フォーム]の処理を実行します。
        * @param context フォームアクションコンテキスト
        */
        invokeBtnokini_frm: function (context) {
            /* このアクションの実行前の拡張処理はここに記述します。*/

			//ADD START 20190327 TJS)takio OM-00381
        	if (f$('c0801w02-acptdd') != null) {
    	        if (f$('c0801w02-acptdd').value == "") {
    	        	var msg = "「受注日」は必ず入力して下さい。";
    	            var buttonsOption = {
    	                'OK': function () {
    	                    $(this).dialog('close');
    	                    f$('c0801w02-acptdd').focus();
    	                }
    	            }
    	            $("#confirm-dialog").html(msg);
    	            $("#confirm-dialog").dialog({
    	                modal: true,
    	                buttons: buttonsOption
    	            });
    	            return false;
    	        }
    	        //10文字でない（日付型に変換できてない）場合
    	        if(f$('c0801w02-acptdd').value.length != 10){
    	        	var msg = "「受注日」は日付として認識できません。";
    	            var buttonsOption = {
    	                'OK': function () {
    	                    $(this).dialog('close');
    	                    f$('c0801w02-acptdd').focus();
    	                }
    	            }
    	            $("#confirm-dialog").html(msg);
    	            $("#confirm-dialog").dialog({
    	                modal: true,
    	                buttons: buttonsOption
    	            });
    	            return false;
    	        }
        	}
        	if (f$('c0801w02-dlvryytidd') != null) {
    	        if (f$('c0801w02-dlvryytidd').value == "") {
    	        	var msg = "「配達予定日」は必ず入力して下さい。";
    	            var buttonsOption = {
    	                'OK': function () {
    	                    $(this).dialog('close');
    	                    f$('c0801w02-dlvryytidd').focus();
    	                }
    	            }
    	            $("#confirm-dialog").html(msg);
    	            $("#confirm-dialog").dialog({
    	                modal: true,
    	                buttons: buttonsOption
    	            });
    	            return false;
    	        }
    	        //10文字でない（日付型に変換できてない）場合
	            /* 2019-12-18 Pkut)Yang OM-00412-000 update begin */
    	        //配達予定日スラッシュ追加編集
	        	dlvryytiddFormat();
	            /* 2019-12-18 Pkut)Yang OM-00412-000 update end   */
    	        if(f$('c0801w02-dlvryytidd').value.length != 10){
    	        	var msg = "「配達予定日」は日付として認識できません。";
    	            var buttonsOption = {
    	                'OK': function () {
    	                    $(this).dialog('close');
    	                    f$('c0801w02-dlvryytidd').focus();
    	                }
    	            }
    	            $("#confirm-dialog").html(msg);
    	            $("#confirm-dialog").dialog({
    	                modal: true,
    	                buttons: buttonsOption
    	            });
    	            return false;
    	        }
        	}
	        //ADD E N D 20190327 TJS)takio

            // フォームアクション実行
            skeleton.actions.invokeServerAction(context, function (context, data, xhr) {
                var fwData = data.__frameworkData;
                /* アクションの実行結果に対する拡張処理はここに記述します。*/

                skeleton.actions.formActionCommonCallBack(context, data, xhr);

            });
        },
        /**
        * フォームアクション[ボタン印刷_ListCREATOR]の処理を実行します。
        * @param context フォームアクションコンテキスト
        */
        invokeBtnprnt_lct: function (context) {
            /* このアクションの実行前の拡張処理はここに記述します。*/
        	/* 2017-12-11 IBIS)inoue add begin */
        	if ($(formUtil.getElementsByFormItemId('kyotensts')).val() != '1') {
        		f$('c0801w02-dlvrykyotencd').disabled = false;
        		f$('c0801w02-dlvryareacd2').disabled = false;
        	}
        	/* 2017-12-11 IBIS)inoue add end */

        	//処理区分によって確認メッセージを出し分ける
        	var msg = "";
        	if (f$('c0801w02-kshnkbn').value != "1") {
        		if (f$('c0801w02-prcskbn')[2].checked) {
        			if (f$('c0801w02-dnpysts').value == "11") {
        				msg = formUtil.getMessageResource("W080", "html", ["配達予定データ", "キャンセル"]);
        			} else {
        				msg = formUtil.getMessageResource("W054", "html", ["受注伝票"]);
        			}
        		} else if (f$('c0801w02-prcskbn')[3].checked) {
        			if (f$('c0801w02-dnpysts').value == "11") {
        				msg = formUtil.getMessageResource("W080", "html", ["配達予定データ", "削除"]);
        			} else {
        				msg = formUtil.getMessageResource("W055", "html", ["受注伝票"]);
        			}
        		} else {
        			if (f$('c0801w02-dnpysts').value == "11") {
        				msg = formUtil.getMessageResource("W080", "html", ["配達予定データ", "更新"]);
        			} else {
        				msg = formUtil.getMessageResource("I001", "html");
        			}
        		}
        	}
        	if (msg != "") {
        		// フォームアクション実行
                skeleton.actions.invokeServerActionWithConfirm(null, msg, context, function (context, data, xhr) {
                    var fwData = data.__frameworkData;
                    /* アクションの実行結果に対する拡張処理はここに記述します。*/

                    skeleton.actions.formActionCommonCallBack(context, data, xhr);
                });
        	}
            else {
                // フォームアクション実行
                skeleton.actions.invokeServerAction(context, function (context, data, xhr) {
                    var fwData = data.__frameworkData;
                    /* アクションの実行結果に対する拡張処理はここに記述します。*/

                    skeleton.actions.formActionCommonCallBack(context, data, xhr);

                });
            }
        },
    	/* 2017-08-16 ibis)shinoda add start */
        /**
         * フォームアクション[ボタン他拠点自拠点切替_他フォーム]の処理を実行します。
         * @param context フォームアクションコンテキスト
         */
         invokeBtntakyotenjikyotenkirikae_frm: function (context) {
             /* このアクションの実行前の拡張処理はここに記述します。*/

             // フォームアクション実行
             skeleton.actions.invokeServerAction(context, function (context, data, xhr) {
                 var fwData = data.__frameworkData;
                 /* アクションの実行結果に対する拡張処理はここに記述します。*/

                 skeleton.actions.formActionCommonCallBack(context, data, xhr);

             });
         },
         /**
         * フォームアクション[ボタンサーバー関連表示_他フォーム]の処理を実行します。
         * @param context フォームアクションコンテキスト
         */
         invokeBtnwsvkanrendsply_frm: function (context) {
             /* このアクションの実行前の拡張処理はここに記述します。*/

             // フォームアクション実行
             skeleton.actions.invokeServerAction(context, function (context, data, xhr) {
                 var fwData = data.__frameworkData;
                 /* アクションの実行結果に対する拡張処理はここに記述します。*/

                 skeleton.actions.formActionCommonCallBack(context, data, xhr);

             });
         },
     	/* 2017-08-16 ibis)shinoda add end */
        /**
        * フォームアクション[リンク連携配達予定参照_クライアントスクリプト]の処理を実行します。
        * @param context フォームアクションコンテキスト
        */
        invokeLinkrenkeidlvryytisansho_spt: function (context) {
            /* このアクションの拡張処理はここに記述します。*/

        },
        /**
        * フォームアクション[リンク連携配達予定担当者_クライアントスクリプト]の処理を実行します。
        * @param context フォームアクションコンテキスト
        */
        invokeLinkrenkeidlvryytitnsha_spt: function (context) {
            /* このアクションの拡張処理はここに記述します。*/

        },
        /**
        * フォームアクション[リンク連携サーバー情報_クライアントスクリプト]の処理を実行します。
        * @param context フォームアクションコンテキスト
        */
        invokeLinkrenkeiwsvjoho_spt: function (context) {
            /* このアクションの拡張処理はここに記述します。*/

        },
        /**
        * 明細共通アクション。明細[M1]のページング処理を実行します。
        * @param context フォームアクションコンテキスト
        */
        invoke_m1_paging: function (context) {
            /* このアクションの実行前の拡張処理はここに記述します。*/

            // フォームアクション実行
            skeleton.actions.invokeServerAction(context, function (context, data, xhr) {
                var fwData = data.__frameworkData;
                /* アクションの実行結果に対する拡張処理はここに記述します。*/

                skeleton.actions.formActionCommonCallBack(context, data, xhr);
            });
        },

        /**
        * 明細共通アクション。明細[M1]のソーティング処理を実行します。
        * @param context フォームアクションコンテキスト
        */
        invoke_m1_sorting: function (context) {
            /* このアクションの実行前の拡張処理はここに記述します。*/

            // フォームアクション実行
            skeleton.actions.invokeServerAction(context, function (context, data, xhr) {
                var fwData = data.__frameworkData;
                /* アクションの実行結果に対する拡張処理はここに記述します。*/

                skeleton.actions.formActionCommonCallBack(context, data, xhr);
            });
        },
        /**
        * この画面専用のカスタムチェック処理を行います。
        */
        customCheck: function (context) {
            return true;
        },
        /**
        * コード参照ウィンドウの呼び出し処理を行います。
        * 呼び出し時及び、呼び出し結果の取得後の拡張処理を実装する場合にはこのメソッドに実装してください。
        * @param {Object} context コードコンテキスト
        */
        invokeCodeReference: function (context) {
        	if(f$('c0801w02-dlvrykyotencd') != undefined)
        		dlvrykyotencd_init = f$('c0801w02-dlvrykyotencd').value;
        	if(f$('c0801w02-dlvryareacd2') != undefined)
        		dlvryareacd_init = f$('c0801w02-dlvryareacd2').value;
            /*コード参照ウィンドウの呼び出し前の拡張処理はここに記述します。*/
            skeleton.actions.invokeCodeActionCommon(context, function (context, data, xhr) {
                var fwData = data.__frameworkData;
                /* コード参照ウィンドウの呼び出しの実行結果に対する拡張処理はここに記述します。*/

                skeleton.actions.codeReferenceCallBack(context, data, xhr);
            });
        },
        /**
        * コード取得処理の呼び出し処理を行います。
        * 呼び出し時及び、呼び出し結果の取得後の拡張処理を実装する場合にはこのメソッドに実装してください。
        * @param {Object} context コードコンテキスト
        */
        invokeCodeGet: function (context) {
            /*コード取得の呼び出し前の拡張処理はここに記述します。*/
            skeleton.actions.invokeCodeActionCommon(context, function (context, data, xhr) {
                var fwData = data.__frameworkData;
                /* コード取得の呼び出しの実行結果に対する拡張処理はここに記述します。*/

                skeleton.actions.codeGetCallBack(context, data, xhr);
            });
        },
        /**
        * 自動コード名称処理の呼び出し処理を行います。
        * 呼び出し時及び、呼び出し結果の取得後の拡張処理を実装する場合にはこのメソッドに実装してください。
        * @param {Object} context コードコンテキスト。
        */
        invokeAutoCodeGet: function (context) {

            /* 自動コード名称取得の呼び出し前の拡張処理はここに記述します。*/
            skeleton.actions.invokeCodeActionCommon(context, function (context, data, xhr) {
                var fwData = data.__frameworkData;
                /* 自動コード名称取得の呼び出しの実行結果に対する拡張処理はここに記述します。*/
                skeleton.actions.autoCodeGetCallBack(context, data, xhr);

            });
        },
        /**
         * 自動コード名称処理の呼び出し処理を行います。
         * 呼び出し時及び、呼び出し結果の取得後の拡張処理を実装する場合にはこのメソッドに実装してください。
         * @param {Object} context コードコンテキスト。
         */
         invokeAutoCodeGetS02m018tpattern: function (context, flg) {

             /* 自動コード名称取得の呼び出し前の拡張処理はここに記述します。*/
        	 /* 2017-12-11 IBIS)inoue add begin */
         	 if ($(formUtil.getElementsByFormItemId('kyotensts')).val() != '1') {
         		 f$('c0801w02-dlvrykyotencd').disabled = false;
         		 f$('c0801w02-dlvryareacd2').disabled = false;
         	 }
         	 /* 2017-12-11 IBIS)inoue add end */
             skeleton.actions.invokeCodeActionCommon(context, function (context, data, xhr) {
                 var fwData = data.__frameworkData;
                 /* 自動コード名称取得の呼び出しの実行結果に対する拡張処理はここに記述します。*/
                 skeleton.actions.autoCodeGetCallBack(context, data, xhr);
                 $(formUtil.getElementsByFormItemId('dlvryareacd')).prop('disabled', flg);

             });
         },
        /**
        * コード参照　出口ルーチン
        * この処理はコード画面の検索結果項目の選択時の親画面へのデータの転送後に実行されます。
        * 拡張処理を実装する場合にはこのメソッドに実装してください。
        * @param {Object} rowIndex コード参照画面で選択したレコードの明細行インデックス
        * @param {Object} codeId コードID
        * @param {Object} formItemId コード実行元項目ID
        * @param {Object} originRowIndex コード実行元項目明細行インデックス
        */
        onCodeDataTransfer: function (rowIndex, codeId, formItemId, originRowIndex) {
            return;
        }
    };

    formInfo.events = events = {
        /**
        * DOMの構築が完了した時のイベント処理を実行します。
        * @param {Object} event イベント情報。
        */
        ready: function (event) {
        },
        /**
        * フォームアクションの呼び出し処理を行います。
        * @param {Object} event イベント情報。
        */
        formAction: function (event) {
            var context = formUtil.getActionContextByEvent(event);
            skeleton.actions.invokeFormAction(context);
        },
        /**
        * コード参照ウィンドウの呼び出し処理を行います。
        * @param {Object} event イベント情報。
        */
        codeReference: function (event) {
            var context = formUtil.getCodeContextByEvent(event, 'codInit');
            actions.invokeCodeReference(context);
        },
        /**
        * コード取得処理の呼び出し処理を行います。
        * @param {Object} event イベント情報。
        */
        codeGet: function (event) {
            var context = formUtil.getCodeContextByEvent(event, 'cog');
            actions.invokeCodeGet(context);
        },
        /**
        * 自動コード名称取得処理を行います。
        * @param {Object} event イベント情報。
        * @param {Object} code コード情報。
        */
        autoCodeGet: function (event, code) {
            var Q_EventTarget = event.currentTarget;
            if(!Q_EventTarget){
             return;
            }
            var itemInfo = formUtil.getFormItemInfoByElement(Q_EventTarget);
            if (itemInfo) {
                switch (itemInfo.itemId.toString().toUpperCase()) {
                    case "DLVRYKYOTENCD":
                    case "DLVRYAREACD":
                        if ("dlvryptnnm_s02m018tpattern" != code.formItemId) {
                            return;
                        }
                        else {
                        	var flg = $(formUtil.getElementsByFormItemId('dlvryareacd')).is(':disabled');
                        	$(formUtil.getElementsByFormItemId('dlvryareacd')).prop('disabled', false);
                        	var context = formUtil.getAutoCodeContext(event, code);
                            actions.invokeAutoCodeGetS02m018tpattern(context, flg);
                            return;
                        }
                        break;
                    default:
                        break;
                }
            }
            else {
                return;
            }
            var context = formUtil.getAutoCodeContext(event, code);
            actions.invokeAutoCodeGet(context);
        },
        /**
        * オンロード処理です。(移植専用)
        */
        user_g_onLoad: function () {
        	//待ちうけ処理
        	  var entryId = f$('c0801w02-entryId').value;
        	  /*  2018-03-30 FJ)Sakihama add begin */
        	  var msgFlg = false;
        	  /*  2018-03-30 FJ)Sakihama add end */
        	  if(entryId != ''){
        	    formDownload(entryId);
        	  }

        		//他画面から遷移した場合、更新後は遷移元に戻る
        		if (f$('c0801w02-syorimode').value == "6") {
        			if (f$('c0801w02-prevscreen').value == 2 || f$('c0801w02-prevscreen').value == 3) {
        				var openProcessFlg = openProcessSubWindow('s0302w00', getBackHomeParam());
        				if (openProcessFlg != false){
        					quitPage();
        				}
        				else {
        					f$('c0801w02-btnmodoru').click();
        				}
        				return false;
        			} else if (f$('c0801w02-prevscreen').value == 4) {
        				var openProcessFlg = openProcessSubWindow('s0402w00', getBackHomeParam() + "&PREVSCREEN=5");
        				if (openProcessFlg != false){
        					quitPage();
        				}
        				else {
        					f$('c0801w02-btnmodoru').click();
        				}
        				return false;
        			}
        		}

        		//クライアントオブジェクト初期化
        	  	initGrid();
        	  	initObject();

        	  	//処理区分の入力制御
        		if (f$('c0801w02-yukokbn').value == "0") {
        			for (var i = 0; i < 4; i++) {
        				f$('c0801w02-prcskbn')[i].disabled = true;
        			}
        		} else if (f$('c0801w02-syorimode').value == "1") {
        			f$('c0801w02-prcskbn')[1].disabled = true;
        			f$('c0801w02-prcskbn')[2].disabled = true;
        			f$('c0801w02-prcskbn')[3].disabled = true;
        			f$('c0801w02-prcskbn')[0].focus();
        		} else {
        			f$('c0801w02-prcskbn')[0].disabled = true;
        			f$('c0801w02-prcskbn')[1].focus();
        		}
        	    //f$('dlvrycmpnynm').focus();

        	  	//配達拠点・エリアの入力制御
        	  	setDlvrykyotenArea();

        		//配達希望時間の入力制御
        		setDlvrykibojikan();

        		//配達希望時間終了の入力制御
        		setDlvrykibojikanshry();


        		//警告メッセージが存在する場合は表示する
        		if (confirmmsg_str != null && confirmmsg_str.length != 0) {
        			var msg = "";
        			msgFlg = true;
        			eval(confirmmsg_str);
        			for (var i = 0; i < confirmmsg_obj.length; i++) {
        				if (i != 0) msg += "</br>";
        				msg += confirmmsg_obj[i];
        			}
        			confirmmsg_str = null;

        			var messageHtml = msg;
                    var buttonsOption;
					buttonsOption = {
							'はい': function () {
								//ダイアログをクローズする。
								$(this).dialog('close');

		        				f$('c0801w02-kshnkbn').value = "1";
		        				if (f$('c0801w02-confirmmsgPrintFg').value == '1') {
		        					f$('c0801w02-btnprnt').click();
		        				} else {
		        					f$('c0801w02-btnkshn').click();
		        				}
					        },
					        'いいえ': function () {
					            //処理が完了した後はダイアログをクローズする。
					        	$(this).dialog('close');
					        	/*  2018-03-30 FJ)Sakihama add begin */
					        	if (f$('c0801w02-meisaiFocusIndex').value == "0") {
					        		$('#c0801w02-dlvryytidd').focus();
			        			} else {
			        				$(formUtil.getElementsByFormItemId('keijokbn', f$('c0801w02-meisaiFocusIndex').value -1)).focus();
			        				f$('c0801w02-meisaiFocusIndex').value = "0";
			        			}
					        	/*  2018-03-30 FJ)Sakihama add end */

					        }
					}
					// <div id="confirm-dialog">をformlayout.jspに記述しておく必要あり。
					$("#confirm-dialog").html(messageHtml);
					$("#confirm-dialog").dialog({
						//dialogClass: "webcat-np-confirmation",
					    modal: true,
					    buttons: buttonsOption,
					    /*  2018-03-30 FJ)Sakihama add begin */
					    open: function() {
					        $( this ).siblings('.ui-dialog-buttonpane').find('button:eq(0)').focus();
					    }
						/*  2018-03-30 FJ)Sakihama add end */

					});
        		}
        		f$('c0801w02-kshnkbn').value = "0";

        		//フォーカスセット
        		/*  2018-03-30 FJ)Sakihama add begin */
        		if(!msgFlg){
        		/*  2018-03-30 FJ)Sakihama add end */
        			if (f$('c0801w02-meisaiFocusIndex').value == "0") {
        				f$('c0801w02-dlvryytidd').focus();
        			} else {
        				$(formUtil.getElementsByFormItemId('keijokbn', f$('c0801w02-meisaiFocusIndex').value -1)).focus();
        				f$('c0801w02-meisaiFocusIndex').value = "0";
        			}
        		/*  2018-03-30 FJ)Sakihama add begin */
        		}
        		/*  2018-03-30 FJ)Sakihama add end */

        	  setMeisaiIdoFlg(true);
        },

        /**
        * オンロード(全画面更新時のみ実行する)処理です。(移植専用)
        */
        user_g_onLoad_once: function () {
        },

        /**
        * アンロード処理です。(移植専用)
        */
        user_g_onUnLoad: function () {
        },

        /**
        * サブミット処理です。(移植専用)
        */
        user_g_onSubmit: function () {

        	//送信直前に入力項目のプロテクトを解除する
        	acpt_obj.unprotectAll();
        	f$('c0801w02-dlvrykibojikan').disabled = false;
        	f$('c0801w02-dlvrykibojikanshry').disabled = false;
        	/* 2018-04-24 ibis)inoue update begin */
        	if ($(formUtil.getElementsByFormItemId('kyotensts')).val() != '1') {
        		f$('c0801w02-dlvryareacd2').disabled = false;
        	}
        	/* 2018-04-24 ibis)inoue update end */
        	return true;
        },

        /**
        * リンク・ボタン・Submit・ラジオボタン・チェックボックスなどクリック処理です。(移植専用)
        * @param ev イベント
        */
        user_g_onClick: function (ev) {
            var Q_EventTarget = ev.currentTarget;
            var itemInfo = formUtil.getFormItemInfoByElement(Q_EventTarget);
            if (itemInfo) {
                switch (itemInfo.itemId.toUpperCase()) {
                    case "REFTNSHACD":
                    	//担当者検索画面を立ち上げる
                    	/* 2017-09-19 ibis)inoue update begin */
                    	 openTantoshaCdSearch($(formUtil.getElementsByFormItemId('apkihoncd')).val()
                    			 ,$(formUtil.getElementsByFormItemId('shopcd')).val()
                    			 ,formUtil.getFormItemInfoByFormItemId('dlvrytnshacd').itemId
                                 ,''
                                 ,''
                                 ,''
                                 ,'1');
                    	 return false;
                    	/* 2017-09-19 ibis)inoue update end */

                    case "LINKRENKEIWSVJOHO":
                    	//得意先サーバー設置情報を立ち上げる
                        openTokuisakiServerList($(formUtil.getElementsByFormItemId('apkihoncd')).val()
                                                , $(formUtil.getElementsByFormItemId('shopcd')).val()
                                                , f$('c0801w02-tokuisakicd').value);
                        return false;
                    /* 2017-10-20 ibis)inoue 横展開No.41対応 delete begin*/
//                    case "ACPTKBN2":
//                    	//受注区分詳細ドロップダウンリストを再生成する
//                        setAcptsys();
//                        /* 2017-08-16 ibis)shinoda add start */
//                        if ($(formUtil.getElementsByFormItemId('acptkbn2'))[0].checked == true ||
//                        		$(formUtil.getElementsByFormItemId('acptkbn2'))[1].checked == true) {
//                        	f$('c0801w02-btnwsvkanrendsply').disabled = true;
//                        } else if ($(formUtil.getElementsByFormItemId('acptkbn2'))[2].checked == true ||
//                        		$(formUtil.getElementsByFormItemId('acptkbn2'))[3].checked == true ||
//                        		$(formUtil.getElementsByFormItemId('acptkbn2'))[4].checked == true) {
//                        	f$('c0801w02-btnwsvkanrendsply').disabled = false;
//                        }
//                        /* 2017-08-16 ibis)shinoda add end */
//                        break;
                    /* 2017-10-20 ibis)inoue 横展開No.41対応 delete end*/
                    case "LINKRENKEIDLVRYYTISANSHO":
                        if ($(formUtil.getElementsByFormItemId('dlvrytnshacd')).val() == '') {
                        	formInfo.skeleton.messages.renderMessageDialog([formUtil.getMessageResource("W003", "html", ["配達担当者コード"]),""]);
            			} else if ($(formUtil.getElementsByFormItemId('dlvrytnshacd')) != null && $(formUtil.getElementsByFormItemId('dlvryytidd')) != null) {
            				//モーダルウィンドウを立ち上げる
            				var contentUrl='../'+programId+'.init'
	            				+ '?PREVSCREEN=5&DLVRYTNSHAFG=0'
	            				+ '&APKIHONCD=' + f$('c0801w02-dlvrykankatsuapkihoncd').value
	            				+ '&SHOPCD=' + f$('c0801w02-dlvrykankatsushopcd').value
	            				+ '&DLVRYCMPNYCD=' + f$('c0801w02-dlvrycmpnynm').options[f$('c0801w02-dlvrycmpnynm').selectedIndex].value
	            				+ '&DLVRYCMPNYNM=' + f$('c0801w02-dlvrycmpnynm').options[f$('c0801w02-dlvrycmpnynm').selectedIndex].text
	            				+ '&DLVRYTNSHACD=' + f$('c0801w02-dlvrytnshacd').value
	            				+ '&DLVRYTNSHANM=' + f$('c0801w02-dlvrytnshamei1').value
	            				+ '&DLVRYYTIDD=' + getItmValue('dlvryytidd')
            					+ '&CHILDTARGETFORMID=c0801w03';
            				var feature = "width=630px, height=610px,menubar=no,toolbar=no,status=no,scrollbars=no,resizable=yes";
            			    var args = new Object();
            				args.title = '配達予定参照';
            				openModalDialog(null, encodeURI(contentUrl), null, feature);
            			}
            	    	return false;

            		case "LINKRENKEIDLVRYYTITNSHA":
            			/* 2017-09-19 ibis)inoue update start */
            			if ($(formUtil.getElementsByFormItemId('kyotensts')).val() != '1') {
            				if ($(formUtil.getElementsByFormItemId('dlvrykyotencd')).prop("selectedIndex") < 0) {
            					formInfo.skeleton.messages.renderMessageDialog([formUtil.getMessageResource("W016", "html", ["配達拠点"]),""]);
            				} else if ($(formUtil.getElementsByFormItemId('dlvryareacd2')).prop("selectedIndex") < 0) {
            					formInfo.skeleton.messages.renderMessageDialog([formUtil.getMessageResource("W016", "html", ["配達拠点"]),""]);
            				} else if (f$('c0801w02-dlvryytidd') != null) {
            					//モーダルウィンドウを立ち上げる
            					var contentUrl='../'+programId+'.init'
	            					+ '?PREVSCREEN=5&DLVRYTNSHAFG=1'
	            					+ '&APKIHONCD=' + f$('c0801w02-dlvrykankatsuapkihoncd').value
		            				+ '&SHOPCD=' + f$('c0801w02-dlvrykankatsushopcd').value
		            				+ '&DLVRYKYOTENCD=' + f$('c0801w02-dlvrykyotencd').options[f$('c0801w02-dlvrykyotencd').selectedIndex].value
		            				+ '&DLVRYKYOTENNM=' + f$('c0801w02-dlvrykyotencd').options[f$('c0801w02-dlvrykyotencd').selectedIndex].text
		            				+ '&DLVRYAREACD=' + f$('c0801w02-dlvryareacd2').options[f$('c0801w02-dlvryareacd2').selectedIndex].value
		            				+ '&DLVRYAREANM=' + f$('c0801w02-dlvryareacd2').options[f$('c0801w02-dlvryareacd2').selectedIndex].text
		            				+ '&DLVRYYTIDD=' + getItmValue('dlvryytidd')
            						+ '&CHILDTARGETFORMID=c0801w03';
            					var feature = "width=630px, height=610px,menubar=no,toolbar=no,status=no,scrollbars=no,resizable=yes";
	            			    var args = new Object();
	            				args.title = '配達予定参照';
	            				openModalDialog(null, encodeURI(contentUrl), null, feature);
	            			}
            			}else{
            				if ($(formUtil.getElementsByFormItemId('dlvrykyotencd2')).prop("selectedIndex") < 0) {
            					formInfo.skeleton.messages.renderMessageDialog([formUtil.getMessageResource("W016", "html", ["配達拠点"]),""]);
            				} else if ($(formUtil.getElementsByFormItemId('dlvryareacd')).prop("selectedIndex") < 0) {
            					formInfo.skeleton.messages.renderMessageDialog([formUtil.getMessageResource("W016", "html", ["配達拠点"]),""]);
            				} else if (f$('c0801w02-dlvryytidd') != null) {
            					//モーダルウィンドウを立ち上げる
            					var contentUrl='../'+programId+'.init'
	            					+ '?PREVSCREEN=5&DLVRYTNSHAFG=1'
	            					+ '&APKIHONCD=' + f$('c0801w02-dlvrykankatsuapkihoncd').value
		            				+ '&SHOPCD=' + f$('c0801w02-dlvrykankatsushopcd').value
		            				+ '&DLVRYKYOTENCD=' + f$('c0801w02-dlvrykyotencd2').options[f$('c0801w02-dlvrykyotencd2').selectedIndex].value
		            				+ '&DLVRYKYOTENNM=' + f$('c0801w02-dlvrykyotencd2').options[f$('c0801w02-dlvrykyotencd2').selectedIndex].text
		            				+ '&DLVRYAREACD=' + f$('c0801w02-dlvryareacd').options[f$('c0801w02-dlvryareacd').selectedIndex].value
		            				+ '&DLVRYAREANM=' + f$('c0801w02-dlvryareacd').options[f$('c0801w02-dlvryareacd').selectedIndex].text
		            				+ '&DLVRYYTIDD=' + getItmValue('dlvryytidd')
            						+ '&CHILDTARGETFORMID=c0801w03';
            					var feature = "width=630px, height=610px,menubar=no,toolbar=no,status=no,scrollbars=no,resizable=yes";
	            			    var args = new Object();
	            				args.title = '配達予定参照';
	            				openModalDialog(null, encodeURI(contentUrl), null, feature);
	            			}
            			}
            			/* 2017-09-19 ibis)inoue update end */
            	    	return false;

            		case 'BTNFUKUSHA':
            		case 'BTNMEISAIDSPLY':
            		case 'BTNOKINI':
            		case 'BTNGYOFUKUSHA':
            			if ($(formUtil.getElementsByFormItemId('acptdd')) == null || $(formUtil.getElementsByFormItemId('dlvryytidd')) == null) {
            				return false;
            			}
            		case 'BTNGYOTSUIKA':
            		case 'BTNGYOSONYU':
            			/* 2017-09-11 ibis)inoue delete begin */
//            			acpt_obj.unprotectAll();
            			/* 2017-09-11 ibis)inoue delete end */
            			break;

            		case "LINKRENKEIHMK1":
            			//配布物検索画面を立ち上げる
            			openHaifubutsuCdSearch($(formUtil.getElementsByFormItemId('apkihoncd')).val()
            					               ,$(formUtil.getElementsByFormItemId('shopcd')).val()
            				                   ,formUtil.getFormItemInfoByFormItemId('haifubutsucd1').itemId
            				                   ,''
            				                   ,''
            				                   ,'1');
            			return false;

            		case "LINKRENKEIHMK2":
            			//配布物検索画面を立ち上げる
            			openHaifubutsuCdSearch($(formUtil.getElementsByFormItemId('apkihoncd')).val()
					               ,$(formUtil.getElementsByFormItemId('shopcd')).val()
				                   ,formUtil.getFormItemInfoByFormItemId('haifubutsucd2').itemId
				                   ,''
				                   ,''
				                   ,'1');
            			return false;

        	    	/* 2017-08-16 ibis)shinoda add start */
            		case "LINKRENKEIHMK3":
            			//配布物検索画面を立ち上げる
            			openHaifubutsuCdSearch($(formUtil.getElementsByFormItemId('apkihoncd')).val()
					               ,$(formUtil.getElementsByFormItemId('shopcd')).val()
				                   ,formUtil.getFormItemInfoByFormItemId('haifubutsucd3').itemId
				                   ,''
				                   ,''
				                   ,'1');
            			return false;

            		case "LINKRENKEIHMK4":
            			//配布物検索画面を立ち上げる
            			openHaifubutsuCdSearch($(formUtil.getElementsByFormItemId('apkihoncd')).val()
					               ,$(formUtil.getElementsByFormItemId('shopcd')).val()
				                   ,formUtil.getFormItemInfoByFormItemId('haifubutsucd4').itemId
				                   ,''
				                   ,''
				                   ,'1');
            			return false;
        	    	/* 2017-08-16 ibis)shinoda add end */

            		case 'BTNPRNT':
            			break;

//            		case 'BTNMODORU':
//            			if ($(formUtil.getElementsByFormItemId('syorimode')).val() != "6") {
//	            			if ($(formUtil.getElementsByFormItemId('prevscreen')).val() == 3) {
//	            				var openProcessFlg = openProcessSubWindow('s0302w00', getBackHomeParam());
//	            				if (openProcessFlg != false){
//	            					quitPage();
//	            				}
//	            				return false;
//	            			} else if (f$('c0801w02-prevscreen').value == 4) {
//	            				var openProcessFlg = openProcessSubWindow('s0402w00', getBackHomeParam() + "&PREVSCREEN=5");
//	            				if (openProcessFlg != false){
//	            					quitPage();
//	            				}
//	            				return false;
//	            			}
//            			}
//            			break;
                    default:
                        break;
                }
            }
        },

        /**
         * ドロップダウンリストなどチェンジ処理です。(移植用)
         * @param ev イベント
         */
        user_g_onChange: function (ev) {
            var Q_EventTarget = ev.currentTarget;
             var itemInfo = formUtil.getFormItemInfoByElement(Q_EventTarget);
             if (itemInfo) {
                 switch (itemInfo.itemId.toUpperCase()) {
                //ここに項目IDのcase文を追加し、固有処理を記述します。
                 case "DLVRYKYOTENCD":
             	  	//配達拠点・エリアの入力制御・配達エリアコンボ要素生成
             		ddl2.setKey();
             	  	setDlvrykyotenArea();
             	  	setDlvryPattern();
                   	break;

             	case "DLVRYAREACD2":
             	  	//配達エリアのパターン取得
             	  	setDlvryPattern();
                   	break;

             	case "DLVRYKIBOJIKANKBN":
             	  	//配達希望時間の入力制御
             		setDlvrykibojikan();
             		//配達希望時間終了の入力制御
             		setDlvrykibojikanshry();
                   	break;

             	case "DLVRYTNSHACD":
             	  	//配達担当者名取得
             		var p = [new PKey("apkihoncd", f$('c0801w02-dlvrykankatsuapkihoncd').value),
                   		   new PKey("shopcd", f$('c0801w02-dlvrykankatsushopcd').value),
                            new PKey("tnshacd", formUtil.formatByItemInfo(formUtil.getFormItemInfoByFormItemId('c0801w02-dlvrytnshacd'), f$('c0801w02-dlvrytnshacd').value))];

             		var f = [new Fieldset("dlvrytnshamei1", "tnshamei")];
             		//getEntity("S02M003T", p, f);
                   	break;

//             	case "DLVRYCMPNYNM":
//             	case "ACPTDD":
//             	case "DLVRYYTIDD":
//
//               		//標準価格のセット
//             		hyujyun_obj.changeAllValue();
//
//               		//販売価格のセット
//             		acpt_obj.changeAllValue();
//
//               		//消費税、金額、合計金額の再計算
//             		recalculate();
//                   	break;

             	case "KEIJOKBN":
             		var mno = formUtil.getRowIndexByElement(Q_EventTarget);

               		//販売価格のセット
             		acpt_obj.changeValue(mno, true);

               		//消費税、金額、合計金額の再計算
             		recalculate(mno);
                   	break;

             	case "ACPTSURYO":
             		var mno = formUtil.getRowIndexByElement(Q_EventTarget);

               		//総数の再計算
             		calc_multiply('acptsuryo', 'uriirisu', 'sokazu', mno);

               		//消費税、金額、合計金額の再計算
             		recalculate(mno);
                   	break;

             	case "ACPTUNT":
             		var mno = formUtil.getRowIndexByElement(Q_EventTarget);

               		//消費税、金額、合計金額の再計算
             		recalculate(mno);
                   	break;
                /* 2017-10-20 ibis)inoue 横展開No.41対応 add begin*/
                case "ACPTKBN2":
                	//受注区分詳細ドロップダウンリストを再生成する
                    setAcptsys();
                    /* 2017-08-16 ibis)shinoda add start */
                    if ($(formUtil.getElementsByFormItemId('acptkbn2'))[0].checked == true ||
                    		$(formUtil.getElementsByFormItemId('acptkbn2'))[1].checked == true) {
                    	f$('c0801w02-btnwsvkanrendsply').disabled = true;
                    } else if ($(formUtil.getElementsByFormItemId('acptkbn2'))[2].checked == true ||
                    		$(formUtil.getElementsByFormItemId('acptkbn2'))[3].checked == true ||
                    		$(formUtil.getElementsByFormItemId('acptkbn2'))[4].checked == true) {
                    	f$('c0801w02-btnwsvkanrendsply').disabled = false;
                    }
                    /* 2017-08-16 ibis)shinoda add end */
                    break;
                /* 2017-10-20 ibis)inoue 横展開No.41対応 add end*/
                default:
                    break;
                }
            }
        },

        /**
         * キープレス処理です。(移植用)
         * @param ev イベント
         */
        user_g_onKeyPress: function (ev){
             var Q_EventTarget = ev.currentTarget;
             if(!Q_EventTarget){
             	return;
             }
             var itemInfo = formUtil.getFormItemInfoByElement(Q_EventTarget.activeElement);
             if (itemInfo) {
                 switch (itemInfo.itemId) {
                //ここに項目IDのcase文を追加し、固有処理を記述します。

                    default:
                        break;
                }
            }
        },


        /**
         * フォーカス処理です。(移植用)
         * @param ev イベント
         */
         user_g_onFocus: function (ev) {
             var Q_EventTarget = ev.currentTarget;
             var itemInfo = formUtil.getFormItemInfoByElement(Q_EventTarget);
         	 var prevVal = formUtil.parseByItemInfo(itemInfo, $(this).val());
         	 $(this).data('val', prevVal);

             if (itemInfo) {
                 switch (itemInfo.itemId) {

                     default:
                         break;
                 }
             }
         },

        /**
         * ブラー処理です。(移植用)
         * @param ev イベント
         */
        user_g_onBlur: function (ev) {
            var Q_EventTarget = ev.currentTarget;
            var itemInfo = formUtil.getFormItemInfoByElement(Q_EventTarget);


            if (itemInfo) {
                switch (itemInfo.itemId.toUpperCase()) {
                case "DLVRYCMPNYNM":
             	case "ACPTDD":
             	case "DLVRYYTIDD":
             		var currentVal = formUtil.parseByItemInfo(itemInfo, $(this).val());
                    var prevVal = $(this).data('val');
                    if(prevVal != currentVal){
                    	//標準価格のセット
                 		hyujyun_obj.changeAllValue();

// ■:1 Isac)Shiba 2020-12-16 価格改定対応（配達予定日変更時に特別単価を再設定）
                         if (itemInfo.itemId.toUpperCase() === 'DLVRYYTIDD') {
                             var rowSize = formUtil.getDisplayRowCountByListId('M1') ;
                             for (var rowNo = 0; rowNo < rowSize; rowNo++) {
                                 var tokubetsuUntListJson = document.getElementById('c0801w02-tokubetsuUntList-json-' + rowNo).value;
                                 if (tokubetsuUntListJson !== undefined && tokubetsuUntListJson !== "") {
                                     try {
                                         // 特別単価リストから配達予定日が適用期間に含まれる単価を販売価格算出用項目にセット
                                         var tokubetsuUntList = JSON.parse(tokubetsuUntListJson);
                                         setItmValue('ttpricestrtdd', '', rowNo);
                                         setItmValue('ttpriceshrydd', '', rowNo);
                                         setItmValue('ttprice', '', rowNo);
                                         setItmValue('ttmochikaeriprice', '', rowNo);
                                         for (var i = 0; i < tokubetsuUntList.length; i++) {
                                             var tokubetsuUnt = tokubetsuUntList[i];
                                             var strtdd = tokubetsuUnt['tekiyostrtdd'];
                                             var shrydd = tokubetsuUnt['tekiyoshrydd'];
                                             if (currentVal >= strtdd && currentVal <= shrydd) {
                                                 setItmValue('ttpricestrtdd', strtdd, rowNo);
                                                 setItmValue('ttpriceshrydd', shrydd, rowNo);
                                                 setItmValue('ttprice', tokubetsuUnt['dlvryprice'], rowNo);
                                                 setItmValue('ttmochikaeriprice', tokubetsuUnt['mochikaeriprice'], rowNo);
                                             }
                                         }
                                     } catch(e) {
                                     }
                                 }
                             }
                         }
// ■:2 Isac)Shiba 2020-12-16 価格改定対応

                   		//販売価格のセット
                 		acpt_obj.changeAllValue();

                   		//消費税、金額、合計金額の再計算
                 		recalculate();
                    }

                   	break;
                    default:
                        break;
                }
            }
        },

        /**
         * コード参照データセット出口ルーチン処理です。(移植用)
         * @param iDataArray データの配列
         * @param iItemId 項目
         * @param iCodeId コード
         */
        user_g_onBeforeCodeSet: function (iDataArray,iItemId,iCodeId) {
        	iDataArray = g_onBeforeCodeSetClearEx(iDataArray);
        	  switch (iItemId.toUpperCase()) {
        		case "DLVRYTNSHACD":
        			f$('c0801w02-dlvrytnshacd').value = iDataArray["code"];
        			f$('c0801w02-dlvrytnshamei1').value = iDataArray["name"];
        	      break;
        		case "HAIFUBUTSUCD1":
        			f$('c0801w02-haifubutsucd1').value = iDataArray["code"];
        			f$('c0801w02-haifubutsunm1').value = iDataArray["name"];
        	      break;
        		case "HAIFUBUTSUCD2":
        			f$('c0801w02-haifubutsucd2').value = iDataArray["code"];
        			f$('c0801w02-haifubutsunm2').value = iDataArray["name"];
        	      break;
    	    	/* 2017-08-16 ibis)shinoda add start */
        		case "HAIFUBUTSUCD3":
        			f$('c0801w02-haifubutsucd3').value = iDataArray["code"];
        			f$('c0801w02-haifubutsunm3').value = iDataArray["name"];
        	      break;
        		case "HAIFUBUTSUCD4":
        			f$('c0801w02-haifubutsucd4').value = iDataArray["code"];
        			f$('c0801w02-haifubutsunm4').value = iDataArray["name"];
        	      break;
    	    	/* 2017-08-16 ibis)shinoda add end */
        	    default:
        	      break;
        	  }
        	  return iDataArray;
        }
    };
    /**
    * イベントの初期化処理を行います。
    */
    setFormEventHandler = function () {
        /*keypressイベント*/
        $(document).on('keypress', events.user_g_onKeyPress);

        /*unloadイベント*/
        $(window).on('unload', events.user_g_onUnLoad);

        var messageSelector = '#' + formId + '----message_area',
            txt = ':text:not([readonly]):not([disabled])',
            tx2 = 'textarea:not([readonly]):not([disabled])',
            pwd = ':password:not([readonly]):not([disabled])',
            btnnc = ':button:not([disabled]):not([data-code-type="cod"]):not([data-code-type="cog"])',
            btn = ':button:not([disabled])',
            sub = ':submit:not([disabled])',
            chk = ':checkbox',
            rdo = ':radio',
            drl = 'select:not([multiple])',
            lst = 'select[multiple]',
            lnk = 'a[href]',
            lnknc = 'a[href]:not([data-code-type="cod"]):not([data-code-type="cog"])',
            fup = ':file',
            clr = ':reset',
            cod = '[data-code-type="cod"]',
            cog = '[data-code-type="cog"]',
            action = '[data-action-id]',
            errorMessage = messageSelector + ' [data-message-type]';
        /*focusイベント*/
        $(document).on('focus', [txt, tx2, pwd, fup, clr, btn, sub, drl, lst].join(', '), events.user_g_onFocus);
        /*clickイベント*/
        $(document).on('click', [btnnc, sub, chk, rdo, lnknc, clr].join(', '), events.user_g_onClick);
        /*changeイベント*/
        $(document).on('change', [txt, tx2, pwd, drl, lst, fup, rdo].join(', '), events.user_g_onChange);
        /*blurイベント*/
        $(document).on('blur', [txt, tx2, pwd, fup, clr, btn, sub, drl, lst].join(', '), events.user_g_onBlur);
        // フレームワーク既定のイベント初期化処理
        skeleton.initialize(formId);

        // readyイベント時の処理をバインドします。
        $(document).on('ready', events.ready);

        // イベント処理を拡張したい場合にはここに記述してください
        //
        // フォーム項目に対するイベント付与の例
        // skeleton.utils.addFormItemEventListener(eventName, [FORM_ITEM_ID], function (event) {[イベント処理]});
        //
        // jQueryによる任意の要素に対するイベント付与の例
        // $(document).on(eventName, selector, function (event) { [イベント処理] });

        /*loadイベント*/
        $(document).on('ifmHtmlUpdated', events.user_g_onLoad);
        $(window).on('load', events.user_g_onLoad_once);

    };

 // 画面オブジェクト初期化
    function initObject() {

    	// コンボボックス初期化
    	relationalComboboxInit();

    	// 受注区分詳細情報初期化
    	eval(acptkbnshsinfo_str);
    	var initacptsys = f$('c0801w02-acptkbnshs').value;
    	setAcptsys();
        for (var i = 0; i < f$('c0801w02-acptkbnshs').options.length; i++) {
        	if (f$('c0801w02-acptkbnshs').options[i].value == initacptsys) {
        		f$('c0801w02-acptkbnshs').options[i].selected = true;
        	}
        }

    	// 価格情報初期化
      	price_obj = new CalcPrice('dlvryytidd', priceinfo_str);
    	hyujyun_obj = new GenerationPrice(
		    			// 2020/11/24　価格改定対応　ISAC)金谷　START
//    					'acptdd',
    					'dlvryytidd',
    					// 2020/11/24　価格改定対応　ISAC)金谷　END
    					'hyojunpricekirikaedd',
    					'genzhyojunprice',
    					'shinhyojunprice',
    					'genzhyojunmochikaeriprice',
    					'shinhyojunmochikaeriprice',
    					'dlvrycmpnynm',
    					'hyojununt');
    	acpt_obj = new SellPrice(
		    			// 2020/11/24　価格改定対応　ISAC)金谷　START
//    					'acptdd',
    					'dlvryytidd',
    					// 2020/11/24　価格改定対応　ISAC)金谷　END
    					'shoppricekirikaedd',
    					'genzshopprice',
    					'shinshopprice',
    					'genzshopmochikaeriprice',
    					'shinshopmochikaeriprice',
    					'dlvryytidd',
		    			// 2020/11/24　価格改定対応　ISAC)金谷　START
//    					'acptdd',
    					'dlvryytidd',
    					// 2020/11/24　価格改定対応　ISAC)金谷　END
    					'hansokustrtdd',
    					'hansokushrydd',
    					'hansokuprice',
    					'hansokumochikaeriprice',
    					'dlvryytidd',
		    			// 2020/11/24　価格改定対応　ISAC)金谷　START
//    					'acptdd',
    					'dlvryytidd',
    					// 2020/11/24　価格改定対応　ISAC)金谷　END
    					'ttpricestrtdd',
    					'ttpriceshrydd',
    					'ttprice',
    					'ttmochikaeriprice',
    					'acptunt',
    					'shokidsplyprice',
    					'keijokbn',
    					'pricekbn',
    					'tokuisakikbn',
    					'tanpinmeisaiflg',
    					'dlvrycmpnynm');

    	//受注単価プロテクト処理
    	acpt_obj.changeAllDisabled();

    	//税込み合計処理
    	recalculate();
    }

    // 金額再計算処理
    function recalculate(mno) {
    	var noTaxFg = (f$('c0801w02-tokuisakikbn').value == KBN_TOKUISAKI_KEIHI);
        /* 2019-05-31 Pkut)Yang 軽減税率対応 add begin */
    	var tokuisakicd = f$('c0801w02-tokuisakicd').value;
        /* 2019-05-31 Pkut)Yang 軽減税率対応 add end */

    	if (mno == null) {
    	    var msize = formUtil.getDisplayRowCountByListId("M1") ;
    		for (var i = 0; i < msize; i++) {
                /* 2019-05-31 Pkut)Yang 軽減税率対応 update begin */
//				price_obj.calcLine(i, 'acptunt', 'acptsuryo', 'kin', 'acptuntshohitaxkin', 'acptshohitaxkin', 'shntaxkbn', 'tokuisakikbn', noTaxFg);
    			if(getItmValue('shntaxkbn',i) != '5'){
    				price_obj.calcLine(i, 'acptunt', 'acptsuryo', 'kin', 'acptuntshohitaxkin', 'acptshohitaxkin', 'shntaxkbn', 'tokuisakikbn', noTaxFg);
    			}else{
    				price_obj.calcLine4('apkihoncd','shopcd',i, 'acptuntshohitaxkin', 'acptshohitaxkin', 'kin','shncd','acptsuryo' ,tokuisakicd,'keijokbn');
    			}
                /* 2019-05-31 Pkut)Yang 軽減税率対応 update end */
    		}

    	} else {
            /* 2019-05-31 Pkut)Yang 軽減税率対応 update begin */
//    		price_obj.calcLine(mno, 'acptunt', 'acptsuryo', 'kin', 'acptuntshohitaxkin', 'acptshohitaxkin', 'shntaxkbn', 'tokuisakikbn', noTaxFg);
			if(getItmValue('shntaxkbn',mno) != '5'){
	    		price_obj.calcLine(mno, 'acptunt', 'acptsuryo', 'kin', 'acptuntshohitaxkin', 'acptshohitaxkin', 'shntaxkbn', 'tokuisakikbn', noTaxFg);
			}else{
				price_obj.calcLine4('apkihoncd','shopcd',mno, 'acptuntshohitaxkin', 'acptshohitaxkin', 'kin','shncd','acptsuryo' ,tokuisakicd,'keijokbn');
			}
            /* 2019-05-31 Pkut)Yang 軽減税率対応 update end */
    	}
		//軽減税率対応 JS ryu 2019-05-24  Start
//    	price_obj.calcAll("M1", 'kin', 'acptkin', 'acptshohitaxkin', 'shohitaxkin', 'sm', 'tokuisakikbn', noTaxFg);
    	price_obj.calcAll("M1", 'kin', 'acptkin', 'acptshohitaxkin', 'shohitaxkin', 'sm', 'tokuisakikbn', noTaxFg,'shntaxkbn');
		//軽減税率対応 JS ryu 2019-05-24  End
    }

    // 受注区分詳細ＤＬセットアップ処理
    function setAcptsys() {
    	var acptkbnObj = f$('c0801w02-acptkbn2');
    	var acptsysObj = f$('c0801w02-acptkbnshs');
    	var acptkbnValue = "";
    	var acptkbnsysValue = f$('c0801w02-acptkbnshsSelected').value;

    	// Optionリストリセット
    	for (var i = 0; i < acptsysObj.options.length; i++) {
    	  acptsysObj.options[i] = null;
    	}
    	acptsysObj.options.length = 0;

    	// 受注区分値取得
    	if (acptkbnObj.length) {
    	    for (var i = 0; i < acptkbnObj.length; i++) {
    	    	if (acptkbnObj[i].checked) {
    	    		acptkbnValue = acptkbnObj[i].value;
    	    	}
    	    }
    	} else {
    		acptkbnValue = acptkbnObj.value;
    	}

    	// Option再構成
        for (var i = 0; i < acptkbnsysinfo_obj.length; i++) {
        	if (acptkbnsysinfo_obj[i].code == acptkbnValue) {
        		var child = acptkbnsysinfo_obj[i].child;
        		for (var j = 0; j < child.length; j++) {
            		acptsysObj.options[j] = new Option(child[j].name, child[j].code);
            		if (child[j].code == acptkbnsysValue) {
            			acptsysObj.options[j].selected = true;
            		}
        		}
        		break;
        	}
        }

        acptsysObj.focus();
    }

    // 配達希望時間入力制御処理
    function setDlvrykibojikan() {

        //配達希望時間区分が「頃」「以降」「迄」の場合は配達希望時間を入力可能にする
    	if (f$('c0801w02-dlvrykibojikankbn').value == "5" || f$('c0801w02-dlvrykibojikankbn').value == "6" || f$('c0801w02-dlvrykibojikankbn').value == "7"
    		|| f$('c0801w02-dlvrykibojikankbn').value == "8") {

    		f$('c0801w02-dlvrykibojikan').disabled = false;
    	} else {
    		f$('c0801w02-dlvrykibojikan').value = "";
    		f$('c0801w02-dlvrykibojikan').disabled = true;
    	}
    }

    // 配達希望時間終了入力制御処理
    function setDlvrykibojikanshry() {

        //配達希望時間区分が指定の場合は配達希望時間を入力可能にする
    	if (f$('c0801w02-dlvrykibojikankbn').value == "8") {
    		f$('c0801w02-dlvrykibojikanshry').disabled = false;
    	} else {
    		f$('c0801w02-dlvrykibojikanshry').value = "";
    		f$('c0801w02-dlvrykibojikanshry').disabled = true;
    	}
    }

    // 配達拠点・エリア入力初期化
    var ddl1 = null; // 配達拠点
    var ddl2 = null; // 配達エリア
    function relationalComboboxInit() {
    	if (f$('c0801w02-dlvrykyotencd') == null) return;

    	ddl1 = new DropDown(f$('c0801w02-dlvrykyotencd'), null);
    	/* 2017-08-29 ibis)hiraishi update begin */
//    	ddl2 = new DropDown(f$('c0801w02-dlvryareacd2'), ddl1);
    	if ($(formUtil.getElementsByFormItemId('kyotensts')).val() == '1') {
    		ddl2 = new DropDown(f$('c0801w02-dlvryareacd'), ddl1);
    	} else {
    		ddl2 = new DropDown(f$('c0801w02-dlvryareacd2'), ddl1);
    	}

//    	setupDropDownList(new Array(ddl1, ddl2), f$("c0801w02-combodata1").value);
    	if ($(formUtil.getElementsByFormItemId('kyotensts')).val() != '1') {
    		setupDropDownList(new Array(ddl1, ddl2), f$("c0801w02-combodata1").value);
    	} else {
    		setupDropDownList(new Array(ddl1, ddl2), f$("c0801w02-combodata2").value);
    	}
    	/* 2017-08-29 ibis)hiraishi update end */
    	ddl1.setKey();
    	ddl1.setSelectedItem(dlvrykyotencd_init);
    	ddl2.setKey();
    	ddl2.setSelectedItem(dlvryareacd_init);
    	/* 2017-08-29 ibis)hiraishi update begin */
//    	ddl2.setSelectedItem(dlvryareacd_init);
//    	if ($(formUtil.getElementsByFormItemId('kyotensts')).val() == '1') {
//    		ddl2.setSelectedItem(dlvryareacd_init);
//    	} else {
//    		ddl2.setSelectedItem(dlvryareacd2_init);
//    	}
    	/* 2017-08-29 ibis)hiraishi update end */
    }

    // 配達拠点・エリア入力制御処理
    function setDlvrykyotenArea() {
        //委託配達の場合は配達拠点・エリアは変更不可とする
    	/* 2017-09-12 ibis)inoue upd begin */
//    	if (f$('c0801w02-dlvryOutsource').value == '1') {
    	/* 2017-08-29 ibis)hiraishi delete begin */
//    		f$('c0801w02-dlvrykyotencd').disabled = true;
    	/* 2017-08-29 ibis)hiraishi delete end */

        //自社配達の場合
//    	} else {
    		/* 2017-08-16 ibis)shinoda add start */
    		if ($(formUtil.getElementsByFormItemId('kyotensts')).val() != '1') {
    		/* 2017-08-16 ibis)shinoda add end */

	    		//新規の場合は得意先の配達エリアを再セットする
	    		if (f$('c0801w02-prcskbn')[0].checked) {
	    		    //配達拠点を変更した場合のみ配達エリアを変更可能とする
	    			if (f$('c0801w02-dlvrykyotencd').value == f$('c0801w02-tokuisakiDlvrykyotencd').value) {
	    				f$('c0801w02-dlvryareacd2').disabled = true;
	        			for (var i = 0; i < f$('c0801w02-dlvryareacd2').options.length; i++) {
	            			if (f$('c0801w02-dlvryareacd2').options[i].value == f$('c0801w02-tokuisakiDlvryareacd').value) {
	            				f$('c0801w02-dlvryareacd2').options[i].selected = true;
	            			}
	        			}
	    			} else {
	    				f$('c0801w02-dlvryareacd2').disabled = false;
	    			}
	    		//新規以外の場合は受注もしくは得意先の配達エリアを再セットする
	    		} else {
	    			//初期表示と同一の拠点の場合は受注の配達エリアを再セットする
	    			if (f$('c0801w02-dlvrykyotencd').value == dlvrykyotencd_init) {
	    				//配達拠点が得意先と同一の場合のみ配達エリアを変更不可能とする
	    				if (f$('c0801w02-dlvrykyotencd').value == f$('c0801w02-tokuisakiDlvrykyotencd').value) {
	    					f$('c0801w02-dlvryareacd2').disabled = true;
	    				} else {
	    					f$('c0801w02-dlvryareacd2').disabled = false;
	    				}
	        			for (var i = 0; i < f$('c0801w02-dlvryareacd2').options.length; i++) {
	            			if (f$('c0801w02-dlvryareacd2').options[i].value == dlvryareacd_init) {
	            				f$('c0801w02-dlvryareacd2').options[i].selected = true;
	            			}
	        			}
	    			//選択した拠点が得意先と同一である場合は得意先の配達エリアを再セットする
	    			} else if (f$('c0801w02-dlvrykyotencd').value == f$('c0801w02-tokuisakiDlvrykyotencd').value) {
	    				f$('c0801w02-dlvryareacd2').disabled = true;
	        			for (var i = 0; i < f$('c0801w02-dlvryareacd2').options.length; i++) {
	            			if (f$('c0801w02-dlvryareacd2').options[i].value == f$('c0801w02-tokuisakiDlvryareacd').value) {
	            				f$('c0801w02-dlvryareacd2').options[i].selected = true;
	            			}
	        			}
	    			} else {
	    				f$('c0801w02-dlvryareacd2').disabled = false;
	    			}

	    		}
    		}
//    	}
		/* 2017-09-12 ibis)inoue upd end */
    }

    // 配達パターン取得処理
    function setDlvryPattern() {
      	//配達エリアのパターン取得
    	/* 2017-09-19 ibis)inoue update start */
		if ($(formUtil.getElementsByFormItemId('kyotensts')).val() != '1') {
	    	var p = [new PKey("apkihoncd", f$('c0801w02-dlvrykankatsuapkihoncd').value),
	         		   new PKey("shopcd", f$('c0801w02-dlvrykankatsushopcd').value),
	                  new PKey("dlvrykyotencd", f$('c0801w02-dlvrykyotencd').value),
	                  new PKey("dlvryareacd", f$('c0801w02-dlvryareacd2').value),
	                 // new PKey("tekiyostrtdd", qqu_GetUnFormat(formUtil.getFormItemInfoByFormItemId('dlvryytidd')))];
	                  new PKey("tekiyostrtdd",f$('c0801w02-dlvryytidd').value.replace(/\//g, ''))];
		}else{
			var p = [new PKey("apkihoncd", f$('c0801w02-dlvrykankatsuapkihoncd').value),
      		   new PKey("shopcd", f$('c0801w02-dlvrykankatsushopcd').value),
               new PKey("dlvrykyotencd", f$('c0801w02-dlvrykyotencd2').value),
               new PKey("dlvryareacd", f$('c0801w02-dlvryareacd').value),
              // new PKey("tekiyostrtdd", qqu_GetUnFormat(formUtil.getFormItemInfoByFormItemId('dlvryytidd')))];
               new PKey("tekiyostrtdd",f$('c0801w02-dlvryytidd').value.replace(/\//g, ''))];
		}
		/* 2017-09-19 ibis)inoue update end */
    	var f = [new Fieldset("dlvryptnnm", "DLVRYPATERN")];
    	//getEntity("S02M018TPATTERN", p, f);
    }

    // 元画面遷移処理
    function getBackHomeParam() {
    	var param = "apkihoncd=";
    	param += f$('c0801w02-apkihoncd').value;
    	param += "&";
    	param += "shopcd=";
    	param += f$('c0801w02-shopcd').value;
    	param += "&";
    	param += "tokuisakicd=";
    	param += f$('c0801w02-tokuisakicd').value;

    	return param;
    }

    // この処理はJavaScriptが読み込まれた時点で実行されます。
    (function () {

        // この画面のイベントの初期化処理を呼び出します。
        setFormEventHandler();

        // メッセージID[MSG_ID]のメッセージリソースをロードします。
        formUtil.loadMessageResource("I001");	// 82
        formUtil.loadMessageResource("W003");	// 87
        formUtil.loadMessageResource("W016");	// 100
        formUtil.loadMessageResource("W054");	// 138
        formUtil.loadMessageResource("W055");	// 139
        formUtil.loadMessageResource("W080");	// 164

    }());

}(window));

/* 2019-12-18 Pkut)Yang OM-00412-000 add begin */
//配達予定日スラッシュ追加編集
function dlvryytiddFormat(){
	var dlvryytiddObj = f$('c0801w02-dlvryytidd');
	var dlvryytiddVal = dlvryytiddObj.value;
	var dateObj;
	if (dlvryytiddVal.length == 8){
		var yyyy = dlvryytiddVal.substr(0,4);
		var mm = dlvryytiddVal.substr(4,2);
		var dd = dlvryytiddVal.substr(6,2);
		dateObj = new Date(yyyy + "/" + mm + "/" + dd);
		if ((yyyy == dateObj.getFullYear()) && (mm == ("0" + (dateObj.getMonth() + 1)).slice(-2)) && (dd == ("0" + dateObj.getDate()).slice(-2))){
			dlvryytiddObj.value = yyyy + "/" + mm + "/" + dd;
		}
	}
}
/* 2019-12-18 Pkut)Yang OM-00412-000 add end   */

//商品検索画面起動処理
function openShohinSearchSub(tsuikaBtnNm) {
	if (f$('c0801w02-acptdd') != null && f$('c0801w02-dlvryytidd') != null) {
		//ADD START 20190327 TJS)takio OM-00381
        if (f$('c0801w02-acptdd').value == "") {
        	var msg = "「受注日」は必ず入力して下さい。";
            var buttonsOption = {
                'OK': function () {
                    $(this).dialog('close');
                    f$('c0801w02-acptdd').focus();
                }
            }
            $("#confirm-dialog").html(msg);
            $("#confirm-dialog").dialog({
                modal: true,
                buttons: buttonsOption
            });
            return false;
        }
        //10文字でない（日付型に変換できてない）場合
        if(f$('c0801w02-acptdd').value.length != 10){
        	var msg = "「受注日」は日付として認識できません。";
            var buttonsOption = {
                'OK': function () {
                    $(this).dialog('close');
                    f$('c0801w02-acptdd').focus();
                }
            }
            $("#confirm-dialog").html(msg);
            $("#confirm-dialog").dialog({
                modal: true,
                buttons: buttonsOption
            });
            return false;
        }
        if (f$('c0801w02-dlvryytidd').value == "") {
        	var msg = "「配達予定日」は必ず入力して下さい。";
            var buttonsOption = {
                'OK': function () {
                    $(this).dialog('close');
                    f$('c0801w02-dlvryytidd').focus();
                }
            }
            $("#confirm-dialog").html(msg);
            $("#confirm-dialog").dialog({
                modal: true,
                buttons: buttonsOption
            });
            return false;
        }
        //10文字でない（日付型に変換できてない）場合
        /* 2019-12-18 Pkut)Yang OM-00412-000 update begin */
        //配達予定日スラッシュ追加編集
    	dlvryytiddFormat();
        /* 2019-12-18 Pkut)Yang OM-00412-000 update end   */
        if(f$('c0801w02-dlvryytidd').value.length != 10){
        	var msg = "「配達予定日」は日付として認識できません。";
            var buttonsOption = {
                'OK': function () {
                    $(this).dialog('close');
                    f$('c0801w02-dlvryytidd').focus();
                }
            }
            $("#confirm-dialog").html(msg);
            $("#confirm-dialog").dialog({
                modal: true,
                buttons: buttonsOption
            });
            return false;
        }
        //ADD E N D 20190327 TJS)takio
		openShohinSearch(f$('c0801w02-apkihoncd').value, f$('c0801w02-shopcd').value, 'c0801w02-shncdadd', tsuikaBtnNm, '', 'uri', '', '', '1');
	} else {
		return false;
	}
}
/**
* コード参照データセット出口ルーチン処理です。(移植用)
* @param iDataArray データの配列
* @param iItemId 項目
* @param iCodeId コード
*/
function g_onBeforeCodeSet(iDataArray,iItemId,iCodeId) {
    var intarfrm = window.fujitsu.intarfrm,
    helpers = intarfrm.helpers,
    actions, events, setFormEventHandler,
    programId = 'c0801w00', formId = 'c0801w02',
    formInfo = intarfrm.forms[formId],
    formUtil = formInfo.formUtil = new intarfrm.FormUtil(programId, formId),
    skeleton = formInfo.skeleton = new intarfrm.Skeleton(programId, formId);
        formInfo.events.user_g_onBeforeCodeSet(iDataArray,iItemId,iCodeId)
}


/* 2017-06-12 FPT)DuongCV [移行]V15対応 add begin */
//Copy from S01AjaxObject

var BGCOLOR_NORMAL = "#fff";
var BGCOLOR_INVALID = "#fdd";

/**
* Ajax用ライブラリ
*/
function AjaxObject(_url, _callback, _afterFunc) {
var that = this;            // クロージャ用
this.updating = false;      // 処理中フラグ
this.fieldset = null;
this.index = -1;

// 処理キャンセル
this.abort = function() {
 if (that.updating) {
   that.updating = false;
   that.Ajax.abort();
   that.Ajax = null;
 }
}

// 非同期呼び出し実行
this.update = function(passData, postMethod) {
 if (that.updating) { return false; }
 that.Ajax = null;
 if (window.XMLHttpRequest) {
   that.Ajax = new XMLHttpRequest();
 } else {
   that.Ajax = new ActiveXObject("Microsoft.XMLHTTP");
 }
 if (that.Ajax == null) { return false; }

 that.Ajax.onreadystatechange = function() {
   if (that.Ajax.readyState == 4) {
     that.updating = false;
     that.callback(that.Ajax.status, that.Ajax.responseText, that.Ajax.responseXML, that.index);
     that.afterFunc();
     that.Ajax = null;
   }
 }

 that.updating = new Date();
 if (/post/i.test(postMethod)) {
   var uri = url+'?'+that.updating.getTime();
   that.Ajax.open("POST", uri, true);
   that.Ajax.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
   that.Ajax.send(passData);
 } else {
   var uri = url+'?'+passData+'&timestamp='+(that.updating.getTime());
   that.Ajax.open("GET", uri, true);
   that.Ajax.send(null);
 }
 return true;
}

var url = _url;
this.callback = _callback || function () { };
this.afterFunc = _afterFunc || function () { };
}

var ajaxObjects = new Array();

//プライマリキーオブジェクト
function PKey(_name, _value) {
this.name = _name;
this.value = _value;
this.length = null;
this.toString = function() {
 return "Name:" + this.name + ",Value:" + this.value;
}
}
//フィールドセットオブジェクト
//画面の項目とＤＢの項目のセット
function Fieldset(_elementName, _columnName) {
this.elementName = _elementName;
if (_columnName) {
 this.columnName = _columnName;
} else {
 this.columnName = _elementName;
}
this.length = null;
this.toString = function() {
 return "ElementName:" + this.elementName + ",ColumnName:" + this.columnName;
}
}

/* 2017-06-12 FPT)DuongCV [移行]V15対応 add end*/
