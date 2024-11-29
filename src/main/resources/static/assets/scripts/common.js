HTMLElement.prototype.hide = function () {
    this.classList.remove('-visible');
    return this;
}

HTMLElement.prototype.show = function () {
    this.classList.add('-visible');
    return this;
}

/**
 * @param {string} dataId
 * @return {HTMLLabelElement}
 */
HTMLFormElement.prototype.findLabel = function (dataId) {
    return this.querySelector(`label.--obj-label[data-id="${dataId}"]`);
}

HTMLLabelElement.prototype.setValid = function (b, warningText) {
    if (b === true) {
        this.classList.remove('-invalid');
    } else if (b === false) {
        this.classList.add('-invalid');
        if (typeof warningText === 'string') {
            this.querySelector(':scope > ._warning').innerText = warningText;
        }
    }
    return this;
}

HTMLLabelElement.prototype.isValid = function () {
    return !this.classList.contains('-invalid');
}

class Dialog {
    /** @type {HTMLElement} */
    static $cover;
    /** @type {Array<HTMLElement>} */
    static $dialogArray = [];

    /**
     * @param {string} title
     * @param {string} content
     * @param {function(HTMLElement)|undefined} onclick
     */
    static defaultOK(title, content, onclick = undefined) {
        Dialog.show({
            title: title,
            content: content,
            buttons: [{text: '확인', onclick: ($dialog) => {
                Dialog.hide($dialog);
                if (typeof onclick === 'function') {
                    onclick($dialog);
                }
                }}]
        })
    }

    /**
     * @param {string} title
     * @param {string} content
     * @param {function(HTMLElement)|undefined} onYes
     * @param {function(HTMLElement)|undefined} onNo
     */
    static defaultYesNo(title, content, onYes = undefined, onNo = undefined) {
        Dialog.show({
            title: title,
            content: content,
            buttons: [
                {
                    text: '선택', onclick: ($dialog) => {
                        Dialog.hide($dialog);
                        if (typeof onYes === 'function') {
                            onYes($dialog);
                        }
                    }
                },
                {
                    text: '취소', onclick: ($dialog) => {
                        Dialog.hide($dialog);
                        if (typeof onNo === 'function') {
                            onNo($dialog);
                        }
                    }
                }
            ]
        })
    }

    /**
     * @param {HTMLElement} $dialog
     */
    static hide($dialog) {
        Dialog.$dialogArray.splice(Dialog.$dialogArray.indexOf($dialog), 1);
        if (Dialog.$dialogArray.length === 0) {
            Dialog.$cover.hide();
        }
        $dialog.hide();
        setTimeout(() => $dialog.remove(), 1000);
    }

    /**
     * @param {Object} args
     * @param {string} args.title
     * @param {string} args.content
     * @param {Array<{string , onclick: function}>|undefined} args.buttons
     * @param {number} delay
     * @returns {HTMLElement}
     */
    static show(args, delay = 50) {
        const $dialog = document.createElement('div');
        $dialog.classList.add('---dialog');
        const $title = document.createElement('div');
        $title.classList.add('_title');
        $title.innerText = args.title;
        const $content = document.createElement('div');
        $content.classList.add('_content');
        $content.innerHTML = args.content;
        $dialog.append($title, $content);
        if (args.buttons != null && args.buttons.length > 0) {
            const $buttonContainer = document.createElement('div');
            $buttonContainer.classList.add('_button-container');
            $buttonContainer.style.gridTemplateColumns = `repeat(${args.buttons.length}, 1fr)`;
            for (const button of args.buttons) {
                const $button = document.createElement('button');
                $button.classList.add('_button');
                $button.setAttribute('type', 'button');
                $button.innerText = button.text;
                if (typeof button.onclick === 'function') {
                    $button.onclick = () => button.onclick($dialog); // 버튼 클릭시, 현재 생성하고 있는 다이얼로그 요소를 인자로 전달해줌.
                }
                $buttonContainer.append($button);
            }
            $dialog.append($buttonContainer);
        }
        document.body.prepend($dialog); // prepend는 제일 앞(첫번째 자식)에 추가.
        Dialog.$dialogArray.push($dialog); // 다이얼로그 배열에 현재 생성한 다이얼로그 추가
        if (Dialog.$cover == null) {
            const $cover = document.createElement('div');
            $cover.classList.add('---dialog-cover');
            document.body.prepend($cover);
            Dialog.$cover = $cover;
        }
        setTimeout(() => {
            $dialog.show();
            Dialog.$cover.show();
        }, delay); // delay 밀리초 뒤에 show 해주는 이유는, 요소 생성 직후 -visible 붙이면, 트랜지션이 안 먹기 때문
        return $dialog;
    }
}

class Loading {
    /** @type {HTMLElement} */
    static $element;

    static hide() {
        Loading.$element?.hide();
    }

    /**
     * @param {number} delay
     */
    static show(delay = 50) {
        if (Loading.$element == null) {
            const $element = document.createElement('div');
            $element.classList.add('---loading');
            const $icon = document.createElement('img');
            $icon.classList.add('_icon');
            $icon.setAttribute('alt', '');
            $icon.setAttribute('src', 'data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAADIAAAAyCAYAAAAeP4ixAAAACXBIWXMAAAsTAAALEwEAmpwYAAAC7ElEQVR4nO3aT6hVVRTH8V1PzTTCf6ApCA0KKnRg0FBUFJ9FZhBB6iQwB6KIUweWk6BEGjUpcCJOnDQoREEoK7KwfxQlr7ICywYROEnTkk9szrpwBtf7zr2e++45j/eFOzpnr/VbZ5+99j5r3ZRmmKbgfmzGYZzC17iMv/EP/sJFvIcjeAYLUhPArBCUxf2nf/KY09iBe0YVwF5cKYm6ifPxtJ/FGqzE/CwSi/BYXDuED2KmOvyOA5g7VUFswnclARdDwKIBbC3AbnxRsvcTxoc9C2+UHP6Ap3BXTfbH8VXJ/pu1zw6W4Vw4uI6Dw3inMYb9uBa+LmBpXcYfwI9h+Dc8UYvh3j5XYyJ8Zt8P3qnBxfg2DH6Wg6pNbTXf50vBDDYzmINPw9A3WFi72sk1zC8Fc2GgNYPXwsCvWDEUpdVnZqKTAPodvB63Ym9YMzSV/a2ZaxHMeD9pNqfWzOHUEBTZrLNeJn/F8GIMyIt8TmoIitTc2WcOTHbzbFyKm59PDQNbQtuVnrOC7aXpG0sNBJ+Hxh29bjoTN+1JDUVxNsuc7nWAuxm/Kd8zqqLQmU/N/3b9nsG6iPRsajh4P7RuvV3a3YOHU8PByxHIkdRmsC0CeTe1GTwagUykNqM4f2X+7HbxBI6nFqA4mWdudLuY01lmVmoB+BAfdbtwNQJp7B5SidKJ95HUZhSFsu6bTJvA6xHIK6nNKCqA02KTGcOrWDtqLTNMa9RU0x0piqLYmaZ+7lYCd5cKEL2rFU1H0S4QRbFVqc3grVJFpe8mTmPAvaXSyyeYNwINs/H4HSceLMfPEczG2hRWr8R/HL6frqtb9cJUf6fgyVI3YMr6MrWjqOpsr631dpu1k9Pzl3gup+ua7NZipx+HuWf+fUx7p2q/Ky/MAWzNxUvxUP7Ir/FwVPf++N8d72+HfQPYOVoa/wuWDEdxtYDy3y+O4aEuT7vTDc6802X8BpzEzpH8haMKuK/UEc68XWngDGl68D/icIn0PTqFnwAAAABJRU5ErkJggg==')
            const $text = document.createElement('span');
            $text.classList.add('_text');
            $text.innerText = '잠시만 기다려 주세요.';
            $element.append($icon, $text);
            document.body.prepend($element);
            Loading.$element = $element;
        }
        setTimeout(() => Loading.$element.show(), delay);
    }
}
