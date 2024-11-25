const $nav = document.getElementById('nav');
const $navItems = Array.from($nav.querySelectorAll(':scope > .menu > .item[rel]'));
const $main = document.getElementById('main');
const $mainContents = Array.from($main.querySelectorAll(':scope > .content[rel]'));

$navItems.forEach(($navItem) => {
    $navItem.onclick = () => {
        const rel = $navItem.getAttribute('rel');
        const $mainContent = $mainContents.find((x) => x.getAttribute('rel') === rel);
        $navItems.forEach((x) => x.classList.remove('-selected'));
        $navItem.classList.add('-selected');
        $mainContents.forEach((x) => x.hide());
        $mainContent.show();
    };
});

{
    const $content = $mainContents.find((x) => x.getAttribute('rel') === 'mymusic.register');
    const $form = $content.querySelector(':scope > form');
    const $melonResultInit = $form.querySelector(':scope > .melon > .row  > .result > .init');
    const $melonResultLoading = $form.querySelector(':scope > .melon > .row > .result > .loading');
    const $melonResultEmpty = $form.querySelector(':scope > .melon > .row > .result > .empty');
    const $melonResultError = $form.querySelector(':scope > .melon > .row > .result > .error');
    let melonSearchTimeout;
    let melonSearchLastKeyword;
    $form['melonKeyword'].addEventListener('keyup', () => {
        $form.querySelectorAll(':scope > .melon > .row > .result > .item').forEach((x) => x.remove());
        $melonResultEmpty.style.display = 'none';
        $melonResultError.style.display = 'none';
        if ($form['melonKeyword'].value === '') {
            $melonResultInit.style.display = 'flex';
            $melonResultLoading.style.display = 'none';
        } else {
            $melonResultInit.style.display = 'none';
            $melonResultLoading.style.display = 'flex';
            if (typeof melonSearchTimeout === 'number') {
                clearTimeout(melonSearchTimeout);
            }
            melonSearchLastKeyword = $form['melonKeyword'].value;
            melonSearchTimeout = setTimeout(() => {
                if (melonSearchLastKeyword !== $form['melonKeyword'].value) {
                    return;
                }
                const xhr = new XMLHttpRequest();
                const url = new URL(location.href);
                url.pathname = "/music/search-melon";
                url.searchParams.set('keyword', $form['melonKeyword'].value);
                xhr.onreadystatechange = () => {
                    if (xhr.readyState !== XMLHttpRequest.DONE) {
                        return;
                    }
                    $melonResultLoading.style.display = 'none';
                    if (xhr.status < 200 || xhr.status >= 300 || xhr.responseText.length === 0) {
                        $melonResultError.style.display = 'flex';
                        return;
                    }
                    const response = JSON.parse(xhr.responseText);
                    if (response.length === 0) {
                        $melonResultEmpty.style.display = 'flex';
                    } else {
                        const $result = $form.querySelector(':scope > .melon > .row > .result');
                        for (const music of response) {
                            const $item = document.createElement('span');
                            $item.classList.add('item');
                            const $image = document.createElement('img');
                            $image.classList.add('image');
                            $image.src = music['coverFileName'];
                            const $column = document.createElement('span');
                            $column.classList.add('column');
                            const $name = document.createElement('span');
                            $name.classList.add('name');
                            $name.innerText = music['name'];
                            const $artist = document.createElement('span');
                            $artist.classList.add('artist');
                            $artist.innerText = music['artist'];
                            $column.append($name, $artist);
                            $item.append($image, $column);
                            $item.onmousedown = () => {
                                $form['melonId'].value = music['youtubeId'];
                                $form['melonCrawlButton'].click();
                            }
                            $result.append($item);
                        }
                    }
                };
                xhr.open('GET', url.toString());
                xhr.send();
            }, 1000);
        }
    })
    $form['melonCrawlButton'].onclick = () => {
        const $melonLabel = $form.findLabel('melon');
        $melonLabel.setValid($form['melonId'].value.length === 8);
        if (!$melonLabel.isValid()) {
            return;
        }
        const xhr = new XMLHttpRequest();
        xhr.onreadystatechange = () => {
            if (xhr.readyState !== XMLHttpRequest.DONE) {
                return;
            }
            Loading.hide();
            if (xhr.status < 200 || xhr.status >= 300) {
                Dialog.show({
                    title: '오류',
                    content: '요청을 전송하는 도중 오류가 발생하였습니다. 잠시 후 다시 시도해 주세요.',
                    buttons: [{
                        text: '확인', onclick: ($dialog) => {
                            Dialog.hide($dialog);
                        }
                    }]
                })
                return;
            }
            if (xhr.responseText.length === 0) {
                Dialog.show({
                    title: '오류',
                    content: `입력하신 멜론 음원 식별자 <b>${$form['melonId'].value}</b>를 통해 음원을 검색할 수 없습니다.<br><br>멜론 음원 식별자를 다시 확인해 주세요.`,
                    buttons: [{text: '확인', onclick: ($dialog) => Dialog.hide($dialog)}]
                });
                return;
            }
            const response = JSON.parse(xhr.responseText);
            const $coverPreviewText = $form.querySelector(':scope > .cover > .row > .preview-wrapper > .text');
            const $coverPreviewImage = $form.querySelector(':scope > .cover > .row > .preview-wrapper > .image');
            $coverPreviewText.style.display = 'none';
            $coverPreviewImage.src = response['coverFileName'];
            $coverPreviewImage.style.display = 'block';
            if (response['youtubeId'] != null) {
                $form['youtubeId'].value = response['youtubeId'];
                $form['youtubeIdCheckButton'].click();
            } else {
                const $text = $form.querySelector(':scope > .youtube > .row > .iframe-wrapper > .text');
                const $iframe = $form.querySelector(':scope > .youtube > .row > .iframe-wrapper > .text');
                $text.style.display = 'flex';
                $iframe.style.display = 'none';
                $form['youtubeId'].value = '';
            }
            $form['artist'].value = response['artist'];
            $form['album'].value = response['album'];
            $form['releaseDate'].value = response['releaseDate'];
            $form['genre'].value = response['genre'];
            $form['name'].value = response['name'];
            $form['lyrics'].value = response['lyrics'];
        };
        xhr.open('GET', `/music/crawl-melon?id=${$form['melonId'].value}`);
        xhr.send();
        Loading.show(0);
    }
    $form['youtubeIdCheckButton'].onclick = () => {
        const youtubeId = $form['youtubeId'].value;
        if (youtubeId.length !== 11) {
            Dialog.show({
                title: '유튜브 식별자 확인',
                content: '올바른 유튜브 식별자를 입력해 주세요',
                buttons: [{
                    text: '확인', onclick: ($dialog) => {
                        Dialog.hide($dialog);
                        $form['youtubeId'].focus();
                        $form['youtubeId'].select();
                    }
                }]
            })
            return;
        }
        const $text = $form.querySelector(':scope > .youtube > .row > .iframe-wrapper > .text');
        const $iframe = $form.querySelector(':scope > .youtube > .row > .iframe-wrapper > .iframe');
        const xhr = new XMLHttpRequest();
        xhr.onreadystatechange = () => {
            if (xhr.readyState !== XMLHttpRequest.DONE) {
                return;
            }
            Loading.hide();
            if (xhr.status < 200 || xhr.status >= 300) {
                Dialog.show({
                    title: '유튜브 식별자 확인',
                    content: '올바른 유튜브 식별자를 입력해 주세요',
                    buttons: [{
                        text: '확인', onclick: ($dialog) => {
                            Dialog.hide($dialog);
                            $form['youtubeId'].focus();
                            $form['youtubeId'].select();
                        }
                    }]
                })
                return;
            }
            const response = JSON.parse(xhr.responseText);
            if (response['result'] === true) {
                $iframe.src = `https://www.youtube.com/embed/${youtubeId}`;
                $iframe.style.display = 'block';
                $text.style.display = 'none';
            } else {
                Dialog.show({
                    title: '유튜브 식별자 확인',
                    content: `입력하신 식별자 <b>${youtubeId}</b>로 조회할 수 있는 영상이 확인되지 않습니다.<br><br>식별자를 다시 확인해 주세요.`,
                    buttons: [{
                        text: '확인', onclick: ($dialog) => {
                            Dialog.hide($dialog);
                            $form['youtubeId'].focus();
                            $form['youtubeId'].select();
                        }
                    }]
                })
            }
        };
        xhr.open('GET', `/music/verify-youtube-id?id=${youtubeId}`);
        xhr.send();
        Loading.show(0);
        $iframe.style.display = 'none';
        $text.style.display = 'flex';
    }
}
