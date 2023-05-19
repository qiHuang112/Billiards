package com.qi.billiards.ui.main

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.animation.addListener
import androidx.core.widget.addTextChangedListener
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.qi.billiards.config.Config
import com.qi.billiards.databinding.DialogAddRuleBinding
import com.qi.billiards.databinding.FragmentRuleMakingBinding
import com.qi.billiards.db.DbUtil
import com.qi.billiards.db.RuleEntity
import com.qi.billiards.ui.base.BaseBindingFragment
import com.qi.billiards.util.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.coroutines.suspendCoroutine

class RuleMakingFragment : BaseBindingFragment<FragmentRuleMakingBinding>() {

    private val args: RuleMakingFragmentArgs by navArgs()
    private val rules = mutableListOf<RuleMakingAdapter.RuleMakingItem>()
    private var searchJob: Job? = null

    override fun getBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentRuleMakingBinding {
        return FragmentRuleMakingBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        binding.tvTitle.text = getTitleFromType(args.gameType)

        binding.rvRule.apply {
            adapter = RuleMakingAdapter(rules, ::editRule, ::selectRules)
            layoutManager = LinearLayoutManager(context)
        }

        binding.fabAdd.setOnClickListener {
            makeNewRule()
        }

        binding.root.setOnClickListener {
            if (rules.isNotEmpty() && rules[0].selectable) {
                rules.forEach { item ->
                    item.selectable = false
                }
                binding.rvRule.adapter?.notifyItemRangeChanged(0, rules.size)
                binding.ivDelete.visibility = View.GONE
            }
        }

        binding.ivDelete.setOnClickListener {
            showDeleteConfirmDialog()
        }

        binding.ivSearch.setOnClickListener {
            showOrHideSearchLayout()
        }

        binding.etSearch.addTextChangedListener(afterTextChanged = {
            searchJob?.cancel()
            searchJob = searchText(it.toString())
        })

        launch {
            if (!get(KEY_HAS_ENTER_RULE_MAKING + args.gameType, false)) {
                save(KEY_HAS_ENTER_RULE_MAKING + args.gameType, true)
                val defaultRules = getDefaultRules(args.gameType)
                rules.addAll(defaultRules)
                binding.rvRule.adapter?.notifyItemRangeInserted(0, rules.size)
                if (defaultRules.isNotEmpty()) {
                    DbUtil.addRules(*defaultRules.map {
                        RuleEntity(it.type, it.content, it.editTime, it.id)
                    }.toTypedArray())
                }
            } else {
                DbUtil.getAllRules(args.gameType).map {
                    RuleMakingAdapter.RuleMakingItem(it.ruleId!!, it.gameType, it.content, it.editTime)
                }.let(rules::addAll)
                binding.rvRule.adapter?.notifyItemRangeInserted(0, rules.size)
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun searchText(key: String) = launch {
        delay(200)
        val searchResult = DbUtil.getRulesByKey(args.gameType, key).map {
            RuleMakingAdapter.RuleMakingItem(it.ruleId!!, it.gameType, it.content, it.editTime, key)
        }
        rules.clear()
        rules.addAll(searchResult)
        binding.rvRule.adapter?.notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun showDeleteConfirmDialog() {
        launch {
            val confirm = getConfirmWithDialog()
            if (confirm) {
                val removedRules =
                    rules.filter { it.isSelected }.map { RuleEntity(it.type, it.content, it.editTime, it.id) }
                rules.removeIf { it.isSelected }
                rules.forEach { it.selectable = false }
                binding.rvRule.adapter?.notifyDataSetChanged()
                binding.ivDelete.visibility = View.GONE
                DbUtil.removeRules(*removedRules.toTypedArray())
            }
        }
    }

    private suspend fun getConfirmWithDialog() = suspendCoroutine { continuation ->
        val dialog = AlertDialog.Builder(context)
            .setPositiveButton("确定") { dialog, _ ->
                continuation.safeResume(true)
                dialog.dismiss()

            }
            .setNegativeButton("取消") { dialog, _ ->
                continuation.safeResume(false)
                dialog.dismiss()

            }
            .create()

        dialog.setOnDismissListener {
            continuation.safeResume(false)
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun showOrHideSearchLayout() {
        val isShow = binding.etSearch.visibility == View.VISIBLE
        if (isShow) {
            val animatorHide = ValueAnimator.ofFloat(50.dp, 0.dp)
            animatorHide.addUpdateListener {
                if (!destroyed) {
                    binding.etSearch.translationY = it.animatedValue as Float
                    binding.rvRule.translationY = it.animatedValue as Float
                }
            }
            animatorHide.addListener(onEnd = {
                if (!destroyed) {
                    binding.etSearch.visibility = View.GONE
                    hideSystemKeyboard(binding.etSearch)
                }
            })
            animatorHide.duration = 200
            animatorHide.start()
        } else {
            val animatorShow = ValueAnimator.ofFloat(0.dp, 50.dp)
            animatorShow.addUpdateListener {
                if (!destroyed) {
                    binding.etSearch.translationY = it.animatedValue as Float
                    binding.rvRule.translationY = it.animatedValue as Float
                }
            }
            animatorShow.addListener(onEnd = {
                if (!destroyed) {
                    binding.etSearch.visibility = View.VISIBLE
                }
            })
            animatorShow.duration = 200
            animatorShow.start()
        }
    }

    private fun editRule(position: Int) {
        launch {
            val rule = rules[position]
            val ruleEntity = getRuleWithDialog(rule)

            if (ruleEntity != null) {
                rule.content = ruleEntity.content
                binding.rvRule.adapter?.notifyItemChanged(position)
                DbUtil.addRule(ruleEntity)
            }
        }
    }

    private fun selectRules() {
        rules.forEach { item ->
            item.selectable = true
        }
        binding.rvRule.adapter?.notifyItemRangeChanged(0, rules.size)

        binding.ivDelete.visibility = View.VISIBLE
    }

    private fun makeNewRule() {
        launch {
            val ruleEntity = getRuleWithDialog()
            if (ruleEntity != null) {
                val id = DbUtil.addRule(ruleEntity)
                rules.add(RuleMakingAdapter.RuleMakingItem(id, args.gameType, ruleEntity.content, ruleEntity.editTime))
                binding.rvRule.adapter?.notifyItemInserted(rules.lastIndex)
            }
        }
    }

    private suspend fun getRuleWithDialog(rule: RuleMakingAdapter.RuleMakingItem? = null) =
        suspendCoroutine { continuation ->
            var dBinding: DialogAddRuleBinding? = DialogAddRuleBinding.inflate(LayoutInflater.from(context))
            val dialogBinding = dBinding!!

            val dialog = AlertDialog.Builder(context)
                .setView(dialogBinding.root)
                .create()

            dialog.setOnDismissListener {
                continuation.safeResume(null)
                dBinding = null
            }
            val title = if (rule == null) "新增规则" else "编辑规则"
            dialogBinding.tvTitle.text = title
            dialogBinding.etInput.setText(rule?.content ?: "")
            dialogBinding.etInput.setSelection(rule?.content?.length ?: 0)

            dialogBinding.tvSave.setOnClickListener {
                val text = dialogBinding.etInput.text.toString()
                if (text.isBlank()) {
                    toast("请输入规则详情")
                } else {
                    continuation.safeResume(RuleEntity(args.gameType, text, System.currentTimeMillis(), rule?.id))
                    dialog.dismiss()
                }
            }
            dialog.show()

        }

    companion object {

        private const val KEY_HAS_ENTER_RULE_MAKING = "KEY_HAS_ENTER_RULE_MAKING_"

        private fun getTitleFromType(type: String): String {
            return when (type) {
                Config.TYPE_ZHUI_FEN.toString() -> "追分规则"
                Config.TYPE_ZHONG_BA.toString() -> "中八规则"
                else -> "不支持的类型"
            }
        }

        private fun getDefaultRules(type: String): List<RuleMakingAdapter.RuleMakingItem> {
            return when (type) {
                Config.TYPE_ZHUI_FEN.toString() -> {
                    Config.ZhuiFen.rules.mapIndexed { index, content ->
                        RuleMakingAdapter.RuleMakingItem((index + 1).toLong(), type, content)
                    }
                }
                Config.TYPE_ZHONG_BA.toString() -> {
                    Config.ZhongBa.rules.mapIndexed { index, content ->
                        RuleMakingAdapter.RuleMakingItem((index + 1).toLong(), type, content)
                    }
                }
                else -> emptyList()
            }
        }
    }
}