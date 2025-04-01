window.addEventListener("load", ()=>{

    let btn = document.querySelectorAll("a.deleteComment");
    let addComment = document.querySelectorAll(".btnComment");

    for (let i = 0; i < addComment.length; i++) {
        addComment[i].addEventListener("click", evt => {
            let childs = addComment[i].parentNode.children;
            let comment = childs[0].value;
            let postId = childs[1].value;
            addComments(parseInt(postId), comment);
        });
    }

    for(let i = 0; i < btn.length; i++) {
        btn[i].addEventListener("click", evt => {
            let parent = document.getElementById(btn[i].getAttribute("name"));
            removeComment(parseInt(btn[i].getAttribute("name").replace(/[^\d]/g, '')), parent);
        });
    }

    function addComments(postId, comment) {
        $.ajax({
            type:'POST',
            data:{postId: postId, comment: comment},
            url : `/social-network/posts/add-comment`,
            success : function(response) {
                addUpdateInfo(response, postId);
            },
            error : function() {
                console.log("Error ajax");
            },
            done : function(e) {
                console.log("DONE");
            }
        });
    }

    function removeComment(idComment, parent) {
        $.ajax({
            type:'POST',
            data:{idComment: idComment},
            url : '/deleteComment_ajax',
            success : function(response) {
                updateInfo(parent, strToObject(response));
            },
            error : function() {
                console.log("Error ajax");
            },
            done : function(e) {
                console.log("DONE");
            }
        });
    }

    function addUpdateInfo(obj, postId) {
        let totalComments = document.getElementById("totalComments-" + postId);
        totalComments.innerHTML = obj.totalComments;

        let commentContainer = document.getElementById("commentContainer-" + postId)

        let divComment = document.createElement("div");
        divComment.className = "row mt-2 p-2 rounded-sm  border-info";
        divComment.setAttribute("id", "comment" + obj.commentId);

        let divContainer = document.createElement("div");
        divContainer.className = "container-fluid";

        let divRow = document.createElement("div");
        divRow.className = "row d-flex justify-content-between";

        let smallTag = document.createElement("small");
        let strongText = document.createElement("strong");
        let aUser = document.createElement("a");
        aUser.setAttribute("href", "/social-network/user-profile/" + obj.userId);
        aUser.innerHTML = obj.userLastName + " " + obj.userFirstName;

        strongText.appendChild(aUser);

        let date = new Date();
        let smallTagTwo = document.createElement("small");
        smallTagTwo.className = "text-muted ml-2 font-weight-light";
        smallTagTwo.innerHTML = obj.date + " в " + date.getHours() + ":" + date.getMinutes() + ":" + date.getSeconds();

        smallTag.appendChild(strongText);
        smallTag.appendChild(smallTagTwo);

        divRow.appendChild(smallTag);

        let divRowTwo = document.createElement("div");
        divRowTwo.className = "row mt-2 pl-3";
        divRowTwo.innerHTML = "<p>" + obj.message + "</p>";

        divContainer.appendChild(divRow);
        divContainer.appendChild(divRowTwo);

        divComment.appendChild(divContainer);
        commentContainer.appendChild(divComment);
    }

    function updateInfo(parent, obj) {
        let totalComments = document.getElementById("totalComments");
        totalComments.innerHTML = obj.totalComments;
        parent.remove();
    }

});