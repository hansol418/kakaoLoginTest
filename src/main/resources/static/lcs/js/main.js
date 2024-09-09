$(function () {
//메인 슬라이드
	$("#main-slide .lazy").slick({
		lazyLoad: 'ondemand', // ondemand progressive anticipated
		infinite: true,
		// autoplay : true,			// 자동 스크롤 사용 여부
		autoplaySpeed: 10000, 		// 자동 스크롤 시 다음으로 넘어가는데 걸리는 시간 (ms)

	});

	//서비스바로가기 롤오버
	$('#Quick .Quick-menu a').each(function () {
		var img = $(this).find('img');
		var src_off = img.attr('src');
		var src_on = src_off.replace('off', 'on');

		$(this).hover(function () {
				img.attr('src', src_on)
			},
			function () {
				img.attr('src', src_off)
			});
	});

//모바일 메뉴
	var menu = $('.mob_menu_wrap a.tit');
	var submenu = $('.mob_menu_wrap a.sub_tit');

	$('.mob_menu_open').click(function () {
		$('#mob_gnb').addClass('on');
		$('.layer_bg').addClass('on');
		$('.mob_gnb_box').css()
		scrollDisable();
	})
	$('.mob_menu_close, .layer_bg').click(function () {
		$('#mob_gnb').removeClass('on');
		$('.layer_bg').removeClass('on');
		scrollAble();
	})


	menu.click(function () {
		menu.not($(this)).removeClass('on');
		$(this).toggleClass('on');

		menu.not($(this)).next('.sub_menu').slideUp('on');
		$(this).next('.sub_menu').slideToggle('on');
	})

	submenu.click(function () {
		submenu.not($(this)).removeClass('on');
		$(this).toggleClass('on');

		submenu.not($(this)).next('.depth_menu').slideUp('on');
		$(this).next('.depth_menu').slideToggle('on');
	})
	//추천교육
	$("#main-triptitle .regular").slick({
		arrows: true,
		infinite: true,
		slidesToShow: 4,
		slidesToScroll: 1,
		autoplay: true,			// 자동 스크롤 사용 여부
		autoplaySpeed: 2000, 		// 자동 스크롤 시 다음으로 넘어가는데 걸리는 시간
		responsive: [
			{
				breakpoint: 1100,
				settings: {
					slidesToShow: 3,
					slidesToScroll: 1
				}
			}, {
				breakpoint: 600,
				settings: {
					slidesToShow: 2,
					slidesToScroll: 1

				}
			}, {
				breakpoint: 450,
				settings: {
					slidesToShow: 1,
					slidesToScroll: 1
				}
			}

		]
	});
	var sw = 0;
	$('.btn_pause').click(function () {
		if (sw == 0) {
			$('.btn_pause').addClass('on');
			$('#main-triptitle .regular').slick('slickPause')
			sw = 1;
		} else {
			$('.btn_pause').removeClass('on');
			$('#main-triptitle .regular').slick('slickPlay')
			sw = 0;
		}
	});
	//공지사항 탭
	$('.tabSet').each(function () {
		var anchor = $(this).find('.tabs a');
		var anchor_on = $(this).find('.tabs a.on');
		var phref_on = anchor_on.attr('phref');
		var this_panel = $(this).find('.panel')

		$(phref_on).show();
		anchor.each(function () {
			var phref = $(this).attr('phref');

			$(this).click(function () {
				this_panel.hide();
				anchor.removeClass('on'); //$('.tabs a')
				$(phref).show();
				$(this).addClass('on');

			}) //click
		}) //anchor
	})  //tabSet


	//기관소개
	$("#information .regular").slick({
		dots: false,
		arrows: true,
		infinite: true,
		slidesToShow: 6,
		slidesToScroll: 1,
		responsive: [
			{
				breakpoint: 1000,
				settings: {
					slidesToShow: 4,
					slidesToScroll: 1
				}
			}, {
				breakpoint: 700,
				settings: {
					slidesToShow: 3,
					slidesToScroll: 1
				}
			}, {
				breakpoint: 500,
				settings: {
					slidesToShow: 2,
					slidesToScroll: 1
				}
			}
		]
	});

	//한줄 후기
	function notice_scroll() {
		$('#rolling_notice li:first').slideUp(function () {
			$(this).appendTo($('#rolling_notice')).slideDown();
		});
	}

	setInterval(function () {
		notice_scroll()
	}, 3500);
//패밀리사이트
	$(document).ready(function () {
		$(".family").click(function () {
			$(".family ul").toggleClass("opacity");
			$(".icon").toggleClass("rotate");
		});
	});

	document.addEventListener('DOMContentLoaded', function() {
		document.getElementById('tripCategory').addEventListener('change', function() {
			const selectedCategory = this.value;
			window.location.href = `/lcs/search-result?category=${selectedCategory}`;
		});
	});

})//ready


